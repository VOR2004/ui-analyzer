package ru.itis.compose.rules.runtime
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.RuntimeAttributes
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class RuntimeSystemAppSnapshotWarningRule(
    private val expectedPackageName: String?
) : Rule {
    override val id: String = RuleIds.RUNTIME_SYSTEM_APP_SNAPSHOT_WARNING

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val expectedPackage = expectedPackageName?.takeIf { packageName -> packageName.isNotBlank() }
            ?: return emptyList()

        return components
            .filter { root -> root.sourceType in runtimeSourceTypes }
            .mapNotNull { root -> analyzeSnapshot(root, expectedPackage) }
    }

    private fun analyzeSnapshot(
        root: UiComponent,
        expectedPackage: String
    ): AnalysisIssue? {
        val packageCounts = ComponentUtils.flatten(root)
            .mapNotNull { component -> component.properties.rawAttributes[RuntimeAttributes.PACKAGE] }
            .filter { packageName -> packageName.isNotBlank() }
            .groupingBy { packageName -> packageName }
            .eachCount()

        if (packageCounts.isEmpty() || packageCounts.containsKey(expectedPackage)) {
            return null
        }

        val actualPackage = packageCounts.maxBy { (_, count) -> count }.key
        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = root.id,
            componentLocator = root.treePath?.let { path -> "${root.type}[path=$path]" },
            componentType = root.type,
            filePath = root.filePath,
            message = AnalyzerMessages.runtimeSystemAppSnapshotWarning(
                expectedPackage = expectedPackage,
                actualPackage = actualPackage
            ),
            recommendation = AnalyzerMessages.RUNTIME_SYSTEM_APP_SNAPSHOT_WARNING_RECOMMENDATION
        )
    }

    private companion object {
        val runtimeSourceTypes = setOf(SourceType.COMPOSE_RUNTIME, SourceType.ANDROID_RUNTIME)
    }
}
