package ru.itis.compose.rules.runtime

import kotlin.math.max
import kotlin.math.min
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.compose.runtime.formatter.ComposeRuntimeComponentFormatter
import ru.itis.compose.runtime.model.ComposeRuntimeAttributes
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiBounds
import ru.itis.model.UiComponent

class ComposeRuntimeOverlappingClickableComponentsRule : Rule {
    override val id: String = RuleIds.COMPOSE_RUNTIME_OVERLAPPING_CLICKABLE_COMPONENTS

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType in runtimeSourceTypes }
            .filter { component -> component.properties.isClickable }
            .filter { component -> component.properties.bounds != null }
            .groupBy { component -> component.runtimeGroupKey() }
            .values
            .flatMap { group -> findOverlaps(group) }
    }

    private fun findOverlaps(components: List<UiComponent>): List<AnalysisIssue> {
        val issues = mutableListOf<AnalysisIssue>()
        for (firstIndex in components.indices) {
            for (secondIndex in firstIndex + 1 until components.size) {
                val first = components[firstIndex]
                val second = components[secondIndex]
                if (first.isAncestorOf(second) || second.isAncestorOf(first)) {
                    continue
                }

                val overlapArea = first.properties.bounds
                    ?.overlapArea(second.properties.bounds)
                    ?: 0f

                if (overlapArea >= MIN_OVERLAP_AREA_PX) {
                    issues += buildIssue(first, second, overlapArea)
                }
            }
        }
        return issues
    }

    private fun buildIssue(
        first: UiComponent,
        second: UiComponent,
        overlapArea: Float
    ): AnalysisIssue {
        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = second.id,
            componentLocator = second.treePath?.let { path -> "${second.type}[path=$path]" },
            componentType = second.type,
            filePath = second.filePath,
            message = AnalyzerMessages.composeRuntimeOverlappingClickableComponents(
                firstComponent = ComposeRuntimeComponentFormatter.describe(first),
                secondComponent = ComposeRuntimeComponentFormatter.describe(second),
                overlapArea = overlapArea.toPixelAreaString()
            ),
            recommendation = AnalyzerMessages
                .COMPOSE_RUNTIME_OVERLAPPING_CLICKABLE_COMPONENTS_RECOMMENDATION
        )
    }

    private fun UiComponent.runtimeGroupKey(): String {
        return listOfNotNull(
            filePath,
            properties.rawAttributes[ComposeRuntimeAttributes.SCREEN],
            properties.rawAttributes[ComposeRuntimeAttributes.STATE]
        ).joinToString(separator = "|")
    }

    private fun UiComponent.isAncestorOf(other: UiComponent): Boolean {
        val currentPath = treePath ?: return false
        val otherPath = other.treePath ?: return false
        return otherPath.startsWith("$currentPath/")
    }

    private fun UiBounds.overlapArea(other: UiBounds?): Float {
        if (other == null) return 0f

        val overlapWidth = min(x + width, other.x + other.width) - max(x, other.x)
        val overlapHeight = min(y + height, other.y + other.height) - max(y, other.y)
        if (overlapWidth <= 0f || overlapHeight <= 0f) return 0f

        return overlapWidth * overlapHeight
    }

    private fun Float.toPixelAreaString(): String {
        return if (this % 1f == 0f) {
            "${toInt()}px2"
        } else {
            "${this}px2"
        }
    }

    private companion object {
        const val MIN_OVERLAP_AREA_PX = 16f
        val runtimeSourceTypes = setOf(SourceType.COMPOSE_RUNTIME, SourceType.ANDROID_RUNTIME)
    }
}
