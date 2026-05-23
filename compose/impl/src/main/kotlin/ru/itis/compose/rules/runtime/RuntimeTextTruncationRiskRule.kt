package ru.itis.compose.rules.runtime

import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.compose.runtime.formatter.ComposeRuntimeComponentFormatter
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiBounds
import ru.itis.model.UiComponent

class RuntimeTextTruncationRiskRule : Rule {
    override val id: String = RuleIds.RUNTIME_TEXT_TRUNCATION_RISK

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType in runtimeSourceTypes }
            .filter { component -> component.isTextLike() }
            .mapNotNull { component -> checkComponent(component) }
    }

    private fun checkComponent(component: UiComponent): AnalysisIssue? {
        val text = component.properties.text
            ?.trim()
            ?.takeIf { value -> value.length >= MIN_TEXT_LENGTH }
            ?: return null
        val bounds = component.properties.bounds ?: return null
        val estimatedTextWidth = text.length * AVERAGE_CHARACTER_WIDTH_PX

        if (estimatedTextWidth <= bounds.width * MAX_WIDTH_USAGE_RATIO && bounds.height >= MIN_TEXT_HEIGHT_PX) {
            return null
        }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.INFO,
            componentId = component.id,
            componentLocator = component.treePath?.let { path -> "${component.type}[path=$path]" },
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerMessages.runtimeTextTruncationRisk(
                component = ComposeRuntimeComponentFormatter.describe(component),
                text = text.take(MAX_TEXT_PREVIEW_LENGTH),
                bounds = bounds.toBoundsString(),
                estimatedWidth = estimatedTextWidth.toPixelString()
            ),
            recommendation = AnalyzerMessages.RUNTIME_TEXT_TRUNCATION_RISK_RECOMMENDATION
        )
    }

    private fun UiComponent.isTextLike(): Boolean {
        return type == ComponentTypes.COMPOSE_TEXT ||
            type == ComponentTypes.TEXT_VIEW ||
            type.endsWith(ComponentTypes.TEXT_VIEW_SUFFIX)
    }

    private fun UiBounds.toBoundsString(): String {
        return "[${x.toPixelString()},${y.toPixelString()}][${(x + width).toPixelString()},${(y + height).toPixelString()}]"
    }

    private fun Float.toPixelString(): String {
        return if (this % 1f == 0f) {
            "${toInt()}px"
        } else {
            "${this}px"
        }
    }

    private companion object {
        const val MIN_TEXT_LENGTH = 8
        const val MAX_TEXT_PREVIEW_LENGTH = 80
        const val AVERAGE_CHARACTER_WIDTH_PX = 7f
        const val MAX_WIDTH_USAGE_RATIO = 1.15f
        const val MIN_TEXT_HEIGHT_PX = 12f
        val runtimeSourceTypes = setOf(SourceType.COMPOSE_RUNTIME, SourceType.ANDROID_RUNTIME)
    }
}
