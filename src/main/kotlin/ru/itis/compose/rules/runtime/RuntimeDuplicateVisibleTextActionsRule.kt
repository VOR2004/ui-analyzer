package ru.itis.compose.rules.runtime

import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class RuntimeDuplicateVisibleTextActionsRule : Rule {
    override val id: String = AnalyzerStrings.RuleIds.RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return components
            .filter { root -> root.sourceType in runtimeSourceTypes }
            .flatMap { root -> analyzeSnapshot(root) }
    }

    private fun analyzeSnapshot(root: UiComponent): List<AnalysisIssue> {
        val clickableEntries = ComponentUtils.flatten(root)
            .filter { component -> component.sourceType in runtimeSourceTypes }
            .filter { component -> component.properties.isClickable }
            .mapNotNull { component ->
                component.accessibleLabel()
                    ?.normalizeLabel()
                    ?.takeIf { label -> label.length >= MIN_LABEL_LENGTH }
                    ?.let { label -> RuntimeActionLabel(component, label) }
            }

        return clickableEntries
            .groupBy { entry -> entry.label }
            .filterValues { entries -> entries.size >= MIN_DUPLICATE_COUNT }
            .map { (label, entries) -> buildIssue(label, entries) }
    }

    private fun buildIssue(
        label: String,
        entries: List<RuntimeActionLabel>
    ): AnalysisIssue {
        val firstComponent = entries.first().component
        return AnalysisIssue(
            ruleId = id,
            severity = Severity.INFO,
            componentId = firstComponent.id,
            componentLocator = firstComponent.treePath?.let { path -> "${firstComponent.type}[path=$path]" },
            componentType = firstComponent.type,
            filePath = firstComponent.filePath,
            message = AnalyzerStrings.Messages.runtimeDuplicateVisibleTextActions(
                label = label,
                count = entries.size,
                examples = entries.take(MAX_EXAMPLES).joinToString { entry -> entry.component.describe() }
            ),
            recommendation = AnalyzerStrings.Messages.RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS_RECOMMENDATION
        )
    }

    private fun UiComponent.accessibleLabel(): String? {
        return properties.contentDescription
            ?: properties.text
            ?: descendantsText().takeIf { text -> text.isNotBlank() }
    }

    private fun UiComponent.descendantsText(): String {
        return children
            .flatMap { child -> listOfNotNull(child.properties.text) + child.descendantsText().takeIf { it.isNotBlank() } }
            .joinToString(separator = " ")
    }

    private fun String.normalizeLabel(): String {
        return trim().replace(WHITESPACE_REGEX, " ")
    }

    private fun UiComponent.describe(): String {
        return listOfNotNull(
            type,
            id?.let { id -> "id=$id" },
            treePath?.let { path -> "path=$path" }
        ).joinToString(prefix = "[", postfix = "]")
    }

    private data class RuntimeActionLabel(
        val component: UiComponent,
        val label: String
    )

    private companion object {
        const val MIN_DUPLICATE_COUNT = 2
        const val MIN_LABEL_LENGTH = 2
        const val MAX_EXAMPLES = 3
        val WHITESPACE_REGEX = Regex("\\s+")
        val runtimeSourceTypes = setOf(SourceType.COMPOSE_RUNTIME, SourceType.ANDROID_RUNTIME)
    }
}
