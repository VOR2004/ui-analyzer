package ru.itis.compose.rules.runtime

import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.compose.runtime.formatter.ComposeRuntimeComponentFormatter
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class RuntimeSmallTouchTargetRule : Rule {
    override val id: String = RuleIds.RUNTIME_SMALL_TOUCH_TARGET

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType in runtimeSourceTypes }
            .filter { component -> component.properties.isClickable }
            .mapNotNull { component -> checkComponent(component) }
    }

    private fun checkComponent(component: UiComponent): AnalysisIssue? {
        val bounds = component.properties.bounds ?: return null
        if (bounds.width >= MIN_TOUCH_TARGET_PX && bounds.height >= MIN_TOUCH_TARGET_PX) {
            return null
        }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = component.id,
            componentLocator = component.treePath?.let { path -> "${component.type}[path=$path]" },
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerMessages.runtimeSmallTouchTarget(
                component = ComposeRuntimeComponentFormatter.describe(component),
                width = bounds.width.toPixelString(),
                height = bounds.height.toPixelString()
            ),
            recommendation = AnalyzerMessages.RUNTIME_SMALL_TOUCH_TARGET_RECOMMENDATION
        )
    }

    private fun Float.toPixelString(): String {
        return if (this % 1f == 0f) {
            "${toInt()}px"
        } else {
            "${this}px"
        }
    }

    private companion object {
        const val MIN_TOUCH_TARGET_PX = 48f
        val runtimeSourceTypes = setOf(SourceType.COMPOSE_RUNTIME, SourceType.ANDROID_RUNTIME)
    }
}
