package ru.itis.compose.rules.accessibility
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeTouchTargetTooSmallRule : Rule {
    override val id: String = RuleIds.COMPOSE_TOUCH_TARGET_TOO_SMALL

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType == SourceType.COMPOSE }
            .filter { component -> component.isInteractive() }
            .mapNotNull { component -> checkComponent(component) }
    }

    private fun checkComponent(component: UiComponent): AnalysisIssue? {
        val width = DimensionUtils.parseDp(component.properties.width)
        val height = DimensionUtils.parseDp(component.properties.height)

        if (width == null || height == null) {
            return null
        }

        if (width >= MIN_TOUCH_TARGET_DP && height >= MIN_TOUCH_TARGET_DP) {
            return null
        }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = component.id,
            componentLocator = component.treePath?.let { path -> "${component.type}[path=$path]" },
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerMessages.composeTouchTargetTooSmall(
                width = component.properties.width,
                height = component.properties.height
            ),
            recommendation = AnalyzerMessages.COMPOSE_TOUCH_TARGET_TOO_SMALL_RECOMMENDATION
        )
    }

    private fun UiComponent.isInteractive(): Boolean {
        return type in interactiveComposeTypes || properties.isClickable
    }

    private companion object {
        const val MIN_TOUCH_TARGET_DP = 48f

        val interactiveComposeTypes = setOf(
            ComponentTypes.COMPOSE_BUTTON,
            ComponentTypes.COMPOSE_ICON_BUTTON,
            ComponentTypes.COMPOSE_OUTLINED_BUTTON,
            ComponentTypes.COMPOSE_TEXT_BUTTON,
            ComponentTypes.COMPOSE_FLOATING_ACTION_BUTTON
        )
    }
}


