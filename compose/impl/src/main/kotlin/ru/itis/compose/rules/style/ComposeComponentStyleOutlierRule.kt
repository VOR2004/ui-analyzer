package ru.itis.compose.rules.style
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.messages.ui.UiPropertyNames

import kotlin.math.abs
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.compose.style.utils.ComposeColorValueNormalizer
import ru.itis.compose.style.signature.ComposeButtonStyleSignature
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeComponentStyleOutlierRule : Rule {
    override val id: String = RuleIds.COMPOSE_COMPONENT_STYLE_OUTLIER

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.flattenAll(components)
            .asSequence()
            .filter { component -> component.sourceType == SourceType.COMPOSE }
            .filter { component -> component.type in composeButtonTypes }
            .mapNotNull { component -> ComposeButtonStyleEntry.from(component) }
            .groupBy { entry -> entry.component.filePath to entry.signature.type }
            .flatMap { (_, entries) -> analyzeButtonGroup(entries) }
            .toList()
    }

    private fun analyzeButtonGroup(entries: List<ComposeButtonStyleEntry>): List<AnalysisIssue> {
        if (entries.size < MIN_COMPONENTS_PER_GROUP) {
            return emptyList()
        }

        val dominantSignature = entries
            .groupingBy { entry -> entry.signature }
            .eachCount()
            .filter { (_, count) -> count >= MIN_DOMINANT_STYLE_FREQUENCY }
            .maxWithOrNull(
                compareBy<Map.Entry<ComposeButtonStyleSignature, Int>> { entry -> entry.value }
                    .thenByDescending { entry -> entry.key.format() }
            )
            ?.key
            ?: return emptyList()

        return entries.mapNotNull { entry ->
            val differences = collectDifferences(
                actual = entry.signature,
                dominant = dominantSignature
            )

            if (differences.size < MIN_DIFFERENCES_TO_REPORT) {
                null
            } else {
                createIssue(entry, differences, dominantSignature)
            }
        }
    }

    private fun collectDifferences(
        actual: ComposeButtonStyleSignature,
        dominant: ComposeButtonStyleSignature
    ): List<String> {
        val differences = mutableListOf<String>()

        if (actual.containerColor != dominant.containerColor) {
            differences += "containerColor"
        }
        if (actual.contentColor != dominant.contentColor) {
            differences += "contentColor"
        }
        if (!sameDp(actual.width, dominant.width)) {
            differences += UiPropertyNames.WIDTH
        }
        if (!sameDp(actual.height, dominant.height)) {
            differences += UiPropertyNames.HEIGHT
        }
        if (!sameDp(actual.padding, dominant.padding)) {
            differences += UiPropertyNames.PADDING
        }

        return differences
    }

    private fun sameDp(first: Float?, second: Float?): Boolean {
        if (first != null || second != null) {
            if (first == null || second == null) return false
            return abs(first - second) <= DIMENSION_TOLERANCE_DP
        }

        return true
    }

    private fun createIssue(
        entry: ComposeButtonStyleEntry,
        differences: List<String>,
        dominantSignature: ComposeButtonStyleSignature
    ): AnalysisIssue {
        val component = entry.component
        return AnalysisIssue(
            ruleId = id,
            severity = if (differences.size >= HIGH_DIFFERENCE_COUNT) Severity.WARNING else Severity.INFO,
            componentId = component.id,
            componentLocator = component.treePath?.let { path -> "${component.type}[path=$path]" },
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerMessages.composeComponentStyleOutlier(
                componentType = component.type,
                differences = differences.joinToString(", ")
            ),
            recommendation = AnalyzerMessages.composeComponentStyleOutlierRecommendation(
                dominantStyle = dominantSignature.format()
            )
        )
    }

    private data class ComposeButtonStyleEntry(
        val component: UiComponent,
        val signature: ComposeButtonStyleSignature
    ) {
        companion object {
            fun from(component: UiComponent): ComposeButtonStyleEntry? {
                val signature = ComposeButtonStyleSignature(
                    type = component.type,
                    containerColor = ComposeColorValueNormalizer.normalize(component.properties.backgroundColor),
                    contentColor = ComposeColorValueNormalizer.normalize(component.properties.textColor),
                    width = DimensionUtils.parseDp(component.properties.width),
                    height = DimensionUtils.parseDp(component.properties.height),
                    padding = DimensionUtils.parseDp(component.properties.padding)
                )

                return signature
                    .takeIf { value -> hasComparableStyleProperty(value) }
                    ?.let { value -> ComposeButtonStyleEntry(component, value) }
            }

            private fun hasComparableStyleProperty(signature: ComposeButtonStyleSignature): Boolean {
                return signature.containerColor != null ||
                    signature.contentColor != null ||
                    signature.width != null ||
                    signature.height != null ||
                    signature.padding != null
            }
        }
    }

    private fun ComposeButtonStyleSignature.format(): String {
        return listOfNotNull(
            "type=$type",
            containerColor?.let { "containerColor=$it" },
            contentColor?.let { "contentColor=$it" },
            width?.let { "width=${it}dp" },
            height?.let { "height=${it}dp" },
            padding?.let { "padding=${it}dp" }
        ).joinToString(", ")
    }

    private companion object {
        const val MIN_COMPONENTS_PER_GROUP = 3
        const val MIN_DOMINANT_STYLE_FREQUENCY = 2
        const val MIN_DIFFERENCES_TO_REPORT = 2
        const val HIGH_DIFFERENCE_COUNT = 3
        const val DIMENSION_TOLERANCE_DP = 1f

        val composeButtonTypes = setOf(
            ComponentTypes.COMPOSE_BUTTON,
            ComponentTypes.COMPOSE_ICON_BUTTON,
            ComponentTypes.COMPOSE_OUTLINED_BUTTON,
            ComponentTypes.COMPOSE_TEXT_BUTTON,
            ComponentTypes.COMPOSE_FLOATING_ACTION_BUTTON
        )
    }
}