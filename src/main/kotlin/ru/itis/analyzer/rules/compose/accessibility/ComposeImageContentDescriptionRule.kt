package ru.itis.analyzer.rules.compose.accessibility

import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.Rule
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeImageContentDescriptionRule : Rule {
    override val id: String = AnalyzerStrings.RuleIds.COMPOSE_IMAGE_CONTENT_DESCRIPTION

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
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerStrings.Messages.composeImageContentDescription(
                componentType = component.type,
                isInteractive = insideInteractiveContainer
            ),
            recommendation = AnalyzerStrings.Messages.COMPOSE_IMAGE_CONTENT_DESCRIPTION_RECOMMENDATION
        )
    }

    private fun isVisualComponent(component: UiComponent): Boolean {
        return component.type == ComponentTypes.COMPOSE_IMAGE ||
            component.type == ComponentTypes.COMPOSE_ICON
    }

    private fun isInteractiveContainer(component: UiComponent): Boolean {
        return component.type in interactiveComposeTypes
    }

    private companion object {
        const val NULL_LITERAL = "null"

        val interactiveComposeTypes = setOf(
            ComponentTypes.COMPOSE_BUTTON,
            ComponentTypes.COMPOSE_ICON_BUTTON,
            ComponentTypes.COMPOSE_OUTLINED_BUTTON,
            ComponentTypes.COMPOSE_TEXT_BUTTON,
            ComponentTypes.COMPOSE_FLOATING_ACTION_BUTTON
        )
    }
}
