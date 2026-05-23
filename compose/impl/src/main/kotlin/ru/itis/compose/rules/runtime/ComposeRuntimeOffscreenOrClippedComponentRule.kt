package ru.itis.compose.rules.runtime

import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.rules.base.Rule
import ru.itis.compose.runtime.formatter.ComposeRuntimeComponentFormatter
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiBounds
import ru.itis.model.UiComponent

class ComposeRuntimeOffscreenOrClippedComponentRule : Rule {
    override val id: String = RuleIds.COMPOSE_RUNTIME_OFFSCREEN_OR_CLIPPED_COMPONENT

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return components
            .filter { root -> root.sourceType in runtimeSourceTypes }
            .flatMap { root ->
                val screenBounds = root.properties.bounds ?: return@flatMap emptyList()
                root.children.flatMap { child -> analyzeNode(child, screenBounds) }
            }
    }

    private fun analyzeNode(component: UiComponent, screenBounds: UiBounds): List<AnalysisIssue> {
        val ownIssue = component.properties.bounds
            ?.let { bounds -> classify(bounds, screenBounds) }
            ?.let { problem -> buildIssue(component, problem, screenBounds) }

        return listOfNotNull(ownIssue) +
            component.children.flatMap { child -> analyzeNode(child, screenBounds) }
    }

    private fun classify(bounds: UiBounds, screenBounds: UiBounds): BoundsProblem? {
        if (bounds.width <= 0f || bounds.height <= 0f) {
            return BoundsProblem(
                severity = Severity.WARNING,
                reason = "non-positive size"
            )
        }

        if (bounds.width < MIN_VISIBLE_SIZE_PX || bounds.height < MIN_VISIBLE_SIZE_PX) {
            return BoundsProblem(
                severity = Severity.INFO,
                reason = "almost zero visible size"
            )
        }

        if (!screenBounds.contains(bounds)) {
            return BoundsProblem(
                severity = Severity.WARNING,
                reason = "outside screen bounds"
            )
        }

        return null
    }

    private fun buildIssue(
        component: UiComponent,
        problem: BoundsProblem,
        screenBounds: UiBounds
    ): AnalysisIssue {
        return AnalysisIssue(
            ruleId = id,
            severity = problem.severity,
            componentId = component.id,
            componentLocator = component.treePath?.let { path -> "${component.type}[path=$path]" },
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerMessages.composeRuntimeOffscreenOrClippedComponent(
                component = ComposeRuntimeComponentFormatter.describe(component),
                bounds = component.properties.bounds.toBoundsString(),
                screenBounds = screenBounds.toBoundsString(),
                reason = problem.reason
            ),
            recommendation = AnalyzerMessages.COMPOSE_RUNTIME_OFFSCREEN_OR_CLIPPED_COMPONENT_RECOMMENDATION
        )
    }

    private fun UiBounds.contains(other: UiBounds): Boolean {
        return other.x >= x - BOUNDS_TOLERANCE_PX &&
            other.y >= y - BOUNDS_TOLERANCE_PX &&
            other.x + other.width <= x + width + BOUNDS_TOLERANCE_PX &&
            other.y + other.height <= y + height + BOUNDS_TOLERANCE_PX
    }

    private fun UiBounds?.toBoundsString(): String {
        if (this == null) return "unknown"
        return "[${x.toPixelString()},${y.toPixelString()}][${(x + width).toPixelString()},${(y + height).toPixelString()}]"
    }

    private fun Float.toPixelString(): String {
        return if (this % 1f == 0f) {
            toInt().toString()
        } else {
            toString()
        }
    }

    private data class BoundsProblem(
        val severity: Severity,
        val reason: String
    )

    private companion object {
        const val MIN_VISIBLE_SIZE_PX = 2f
        const val BOUNDS_TOLERANCE_PX = 1f
        val runtimeSourceTypes = setOf(SourceType.COMPOSE_RUNTIME, SourceType.ANDROID_RUNTIME)
    }
}
