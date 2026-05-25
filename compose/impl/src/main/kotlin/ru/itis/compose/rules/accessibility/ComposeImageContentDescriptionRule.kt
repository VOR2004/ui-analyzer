package ru.itis.compose.rules.accessibility
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.rules.base.Rule
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeImageContentDescriptionRule : Rule {
    override val id: String = RuleIds.COMPOSE_IMAGE_CONTENT_DESCRIPTION

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return components.flatMap { root -> checkComponent(root, parent = null) }
    }

    private fun checkComponent(
        component: UiComponent,
        parent: UiComponent?
    ): List<AnalysisIssue> {
        val ownIssue = if (component.sourceType == SourceType.COMPOSE && isVisualComponent(component)) {
            createIssueIfNeeded(component, parent)
        } else {
            null
        }

        return listOfNotNull(ownIssue) +
            component.children.flatMap { child -> checkComponent(child, component) }
    }

    private fun createIssueIfNeeded(
        component: UiComponent,
        parent: UiComponent?
    ): AnalysisIssue? {
        val contentDescription = component.properties.contentDescription?.trim()
        val insideInteractiveContainer = parent?.let { isInteractiveContainer(it) } == true

        if (!contentDescription.isNullOrBlank() && contentDescription != NULL_LITERAL) {
            return null
        }

        val severity = if (insideInteractiveContainer) Severity.WARNING else Severity.INFO

        return AnalysisIssue(
            ruleId = id,
            severity = severity,
            componentId = component.id,
            componentLocator = buildComponentLocator(component),
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerMessages.composeImageContentDescription(
                componentType = component.type,
                isInteractive = insideInteractiveContainer
            ),
            recommendation = AnalyzerMessages.COMPOSE_IMAGE_CONTENT_DESCRIPTION_RECOMMENDATION
        )
    }

    private fun isVisualComponent(component: UiComponent): Boolean {
        return component.type == ComponentTypes.COMPOSE_IMAGE ||
            component.type == ComponentTypes.COMPOSE_ICON
    }

    private fun isInteractiveContainer(component: UiComponent): Boolean {
        return component.type in interactiveComposeTypes
    }

    private fun buildComponentLocator(component: UiComponent): String {
        val details = listOfNotNull(
            component.id?.let { value -> "id=$value" },
            component.properties.rawAttributes[COMPOSE_FUNCTION_ATTRIBUTE]?.let { value -> "composable=$value" },
            component.properties.rawAttributes[VISUAL_SOURCE_ATTRIBUTE]?.let { value ->
                "visualSource=${value.take(MAX_LOCATOR_VALUE_LENGTH)}"
            },
            component.properties.rawAttributes[IMAGE_VECTOR_ATTRIBUTE]?.let { value ->
                "imageVector=${value.take(MAX_LOCATOR_VALUE_LENGTH)}"
            },
            component.properties.rawAttributes[PAINTER_ATTRIBUTE]?.let { value ->
                "painter=${value.take(MAX_LOCATOR_VALUE_LENGTH)}"
            },
            component.treePath?.let { value -> "path=$value" },
            component.properties.contentDescription?.let { value ->
                "contentDescription=${value.take(MAX_LOCATOR_VALUE_LENGTH)}"
            }
        )

        return if (details.isEmpty()) {
            component.type
        } else {
            "${component.type}[${details.joinToString(", ")}]"
        }
    }

    private companion object {
        const val NULL_LITERAL = "null"
        const val MAX_LOCATOR_VALUE_LENGTH = 40
        const val COMPOSE_FUNCTION_ATTRIBUTE = "compose:function"
        const val VISUAL_SOURCE_ATTRIBUTE = "visualSource"
        const val IMAGE_VECTOR_ATTRIBUTE = "imageVector"
        const val PAINTER_ATTRIBUTE = "painter"

        val interactiveComposeTypes = setOf(
            ComponentTypes.COMPOSE_BUTTON,
            ComponentTypes.COMPOSE_ICON_BUTTON,
            ComponentTypes.COMPOSE_OUTLINED_BUTTON,
            ComponentTypes.COMPOSE_TEXT_BUTTON,
            ComponentTypes.COMPOSE_FLOATING_ACTION_BUTTON
        )
    }
}

