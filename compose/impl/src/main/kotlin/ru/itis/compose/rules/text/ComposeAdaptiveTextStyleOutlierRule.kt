package ru.itis.compose.rules.text
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.messages.ui.UiPropertyNames

import kotlin.math.abs
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.compose.source.analyzer.ComposeTextRolePredictor
import ru.itis.compose.style.signature.ComposePredictedTextRole

class ComposeAdaptiveTextStyleOutlierRule : Rule {
    override val id: String = RuleIds.COMPOSE_ADAPTIVE_TEXT_STYLE_OUTLIER
    private val rolePredictor = ComposeTextRolePredictor()

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType == SourceType.COMPOSE }
            .filter { component -> component.type == ComponentTypes.COMPOSE_TEXT }
            .groupBy { component -> component.filePath }
            .flatMap { (_, textComponents) -> analyzeFile(textComponents) }
    }

    private fun analyzeFile(textComponents: List<UiComponent>): List<AnalysisIssue> {
        val entries = textComponents.mapNotNull { component ->
            ComposeTextStyleEntry.from(
                component = component,
                role = rolePredictor.predict(component)
            )
        }
        if (entries.size < MIN_TEXTS_PER_FILE) {
            return emptyList()
        }

        return entries
            .groupBy { entry -> entry.role }
            .flatMap { (_, roleEntries) -> analyzeRole(roleEntries) }
    }

    private fun analyzeRole(entries: List<ComposeTextStyleEntry>): List<AnalysisIssue> {
        if (entries.size < MIN_TEXTS_PER_ROLE) {
            return emptyList()
        }

        val dominantStyle = entries
            .groupingBy { entry -> entry.signature }
            .eachCount()
            .filter { (_, count) -> count >= MIN_DOMINANT_STYLE_FREQUENCY }
            .maxByOrNull { (_, count) -> count }
            ?.key
            ?: return emptyList()

        return entries.mapNotNull { entry ->
            val differences = collectDifferences(entry.signature, dominantStyle)
            if (differences.isEmpty()) {
                null
            } else {
                createIssue(entry, differences, dominantStyle)
            }
        }
    }

    private fun collectDifferences(
        actual: ComposeTextStyleSignature,
        dominant: ComposeTextStyleSignature
    ): List<String> {
        val differences = mutableListOf<String>()

        if (actual.typographyStyle != dominant.typographyStyle) {
            differences += "typographyStyle"
        }
        if (!sameSp(actual.textSize, dominant.textSize)) {
            differences += UiPropertyNames.TEXT_SIZE
        }
        if (actual.textStyle != dominant.textStyle) {
            differences += "textStyle"
        }
        if (actual.fontFamily != dominant.fontFamily) {
            differences += "fontFamily"
        }

        return differences
    }

    private fun sameSp(first: String?, second: String?): Boolean {
        val firstSp = DimensionUtils.parseSp(first)
        val secondSp = DimensionUtils.parseSp(second)

        if (firstSp != null || secondSp != null) {
            if (firstSp == null || secondSp == null) return false
            return abs(firstSp - secondSp) <= TEXT_SIZE_TOLERANCE_SP
        }

        return first?.trim() == second?.trim()
    }

    private fun createIssue(
        entry: ComposeTextStyleEntry,
        differences: List<String>,
        dominantStyle: ComposeTextStyleSignature
    ): AnalysisIssue {
        val component = entry.component
        return AnalysisIssue(
            ruleId = id,
            severity = if (differences.size >= HIGH_DIFFERENCE_COUNT) Severity.WARNING else Severity.INFO,
            componentId = component.id,
            componentLocator = component.treePath?.let { path -> "${component.type}[path=$path]" },
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerMessages.composeAdaptiveTextStyleOutlier(
                differences = differences.joinToString(", "),
                predictedRole = entry.role.name
            ),
            recommendation = AnalyzerMessages.composeAdaptiveTextStyleOutlierRecommendation(
                dominantStyle = "predictedRole=${entry.role.name}, ${dominantStyle.format()}"
            )
        )
    }

    private data class ComposeTextStyleEntry(
        val component: UiComponent,
        val role: ComposePredictedTextRole,
        val signature: ComposeTextStyleSignature
    ) {
        companion object {
            fun from(
                component: UiComponent,
                role: ComposePredictedTextRole
            ): ComposeTextStyleEntry? {
                val signature = ComposeTextStyleSignature(
                    typographyStyle = component.properties.typographyStyle?.trim(),
                    textSize = component.properties.textSize?.trim(),
                    textStyle = component.properties.textStyle?.trim(),
                    fontFamily = component.properties.fontFamily?.trim()
                )

                return signature
                    .takeIf { value -> value.hasAnyStyleProperty }
                    ?.let { value -> ComposeTextStyleEntry(component, role, value) }
            }
        }
    }

    private data class ComposeTextStyleSignature(
        val typographyStyle: String?,
        val textSize: String?,
        val textStyle: String?,
        val fontFamily: String?
    ) {
        val hasAnyStyleProperty: Boolean
            get() = typographyStyle != null || textSize != null || textStyle != null || fontFamily != null

        fun format(): String {
            return listOfNotNull(
                typographyStyle?.let { "typographyStyle=$it" },
                textSize?.let { "textSize=$it" },
                textStyle?.let { "textStyle=$it" },
                fontFamily?.let { "fontFamily=$it" }
            ).joinToString(", ")
        }
    }

    private companion object {
        const val MIN_TEXTS_PER_FILE = 4
        const val MIN_TEXTS_PER_ROLE = 2
        const val MIN_DOMINANT_STYLE_FREQUENCY = 2
        const val HIGH_DIFFERENCE_COUNT = 2
        const val TEXT_SIZE_TOLERANCE_SP = 1f
    }
}


