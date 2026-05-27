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

class RuntimeDuplicateVisibleTextActionsRule : Rule {
    override val id: String = RuleIds.RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS

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
            message = AnalyzerMessages.runtimeDuplicateVisibleTextActions(
                label = label,
                count = entries.size,
                examples = entries.take(MAX_EXAMPLES).joinToString { entry -> entry.component.describe() }
            ),
            recommendation = AnalyzerMessages.RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS_RECOMMENDATION
        )
    }

    private fun UiComponent.accessibleLabel(): String? {
        return properties.contentDescription?.cleanRuntimeText()
            ?: properties.text?.cleanRuntimeText()
            ?: descendantsText().cleanRuntimeText()
    }

    private fun UiComponent.descendantsText(): String {
        return children
            .flatMap { child ->
                listOfNotNull(child.properties.text?.cleanRuntimeText()) +
                    listOfNotNull(child.descendantsText().cleanRuntimeText())
            }
            .joinToString(separator = " ")
    }

    private fun String.cleanRuntimeText(): String? {
        return trim()
            .takeIf { value -> value.isNotBlank() }
            ?.takeUnless { value -> value.equals(NULL_TEXT, ignoreCase = true) }
    }

    private fun String.normalizeLabel(): String {
        return trim().replace(WHITESPACE_REGEX, " ")
    }

    private fun UiComponent.describe(): String {
        return ComposeRuntimeComponentFormatter.describe(this)
    }

    private data class RuntimeActionLabel(
        val component: UiComponent,
        val label: String
    )

    private companion object {
        const val MIN_DUPLICATE_COUNT = 2
        const val MIN_LABEL_LENGTH = 2
        const val MAX_EXAMPLES = 3
        const val NULL_TEXT = "null"
        val WHITESPACE_REGEX = Regex("\\s+")
        val runtimeSourceTypes = setOf(SourceType.COMPOSE_RUNTIME, SourceType.ANDROID_RUNTIME)
    }
}

