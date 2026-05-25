package ru.itis.report

import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent

internal class AnalysisReportBuilder {

    fun build(
        components: List<UiComponent>,
        issues: List<AnalysisIssue>
    ): AnalysisReport {
        val enrichedIssues = enrichIssuesWithLocators(components, issues)

        return AnalysisReport(
            summary = Summary(
                totalComponents = components.sumOf { component -> countComponents(component) },
                totalIssues = enrichedIssues.size
            ),
            components = components,
            issues = enrichedIssues
        )
    }

    private fun enrichIssuesWithLocators(
        components: List<UiComponent>,
        issues: List<AnalysisIssue>
    ): List<AnalysisIssue> {
        val flattenedComponents = components.flatMap { component -> flatten(component) }
        val componentsByKey = flattenedComponents.groupBy { component -> component.matchKey() }
        val issueCounters = mutableMapOf<IssueMatchKey, Int>()

        return issues.map { issue ->
            if (issue.componentLocator != null) {
                issue
            } else {
                val component = findComponentForIssue(componentsByKey, issue, issueCounters)
                issue.copy(componentLocator = component?.let { buildComponentLocator(it) })
            }
        }
    }

    private fun findComponentForIssue(
        componentsByKey: Map<ComponentMatchKey, List<UiComponent>>,
        issue: AnalysisIssue,
        issueCounters: MutableMap<IssueMatchKey, Int>
    ): UiComponent? {
        val issueKey = issue.matchKey()
        val componentCandidates = componentsByKey[issueKey.componentKey].orEmpty()
        if (componentCandidates.isEmpty()) return null

        val index = issueCounters.getOrDefault(issueKey, 0)
        issueCounters[issueKey] = index + 1

        return componentCandidates.getOrElse(index) { componentCandidates.last() }
    }

    private fun buildComponentLocator(component: UiComponent): String {
        val details = listOfNotNull(
            component.id?.let { "id=$it" },
            component.properties.rawAttributes[COMPOSE_FUNCTION_ATTRIBUTE]?.let { "composable=$it" },
            component.properties.rawAttributes[VISUAL_SOURCE_ATTRIBUTE]?.let {
                "visualSource=${it.take(MAX_LOCATOR_VALUE_LENGTH)}"
            },
            component.properties.rawAttributes[IMAGE_VECTOR_ATTRIBUTE]?.let {
                "imageVector=${it.take(MAX_LOCATOR_VALUE_LENGTH)}"
            },
            component.properties.rawAttributes[PAINTER_ATTRIBUTE]?.let {
                "painter=${it.take(MAX_LOCATOR_VALUE_LENGTH)}"
            },
            component.treePath?.let { "path=$it" },
            component.properties.text?.let { "text=${it.take(MAX_LOCATOR_VALUE_LENGTH)}" },
            component.properties.textSize?.let { "textSize=$it" },
            component.properties.textStyle?.let { "textStyle=$it" },
            component.properties.contentDescription?.let {
                "contentDescription=${it.take(MAX_LOCATOR_VALUE_LENGTH)}"
            }
        )

        return if (details.isEmpty()) {
            component.type
        } else {
            "${component.type}[${details.joinToString(", ")}]"
        }
    }

    private fun flatten(component: UiComponent): List<UiComponent> {
        return listOf(component) + component.children.flatMap { child -> flatten(child) }
    }

    private fun countComponents(component: UiComponent): Int {
        return 1 + component.children.sumOf { child -> countComponents(child) }
    }

    private fun UiComponent.matchKey(): ComponentMatchKey {
        return ComponentMatchKey(
            filePath = filePath,
            componentType = type,
            componentId = id
        )
    }

    private fun AnalysisIssue.matchKey(): IssueMatchKey {
        return IssueMatchKey(
            ruleId = ruleId,
            componentKey = ComponentMatchKey(
                filePath = filePath,
                componentType = componentType,
                componentId = componentId
            )
        )
    }

    private data class ComponentMatchKey(
        val filePath: String,
        val componentType: String,
        val componentId: String?
    )

    private data class IssueMatchKey(
        val ruleId: String,
        val componentKey: ComponentMatchKey
    )

    private companion object {
        const val MAX_LOCATOR_VALUE_LENGTH = 40
        const val COMPOSE_FUNCTION_ATTRIBUTE = "compose:function"
        const val VISUAL_SOURCE_ATTRIBUTE = "visualSource"
        const val IMAGE_VECTOR_ATTRIBUTE = "imageVector"
        const val PAINTER_ATTRIBUTE = "painter"
    }
}
