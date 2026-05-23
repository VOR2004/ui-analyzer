package ru.itis.compose.rules.text

import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.analyzer.utils.NearDuplicateDimensionAnalyzer
import ru.itis.compose.style.role.ComposeTextRolePredictor
import ru.itis.compose.style.role.DefaultComposeTextRolePredictor
import ru.itis.compose.style.signature.ComposePredictedTextRole
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeTextSizeNearDuplicateClusterRule : Rule {
    override val id: String = RuleIds.COMPOSE_TEXT_SIZE_NEAR_DUPLICATE_CLUSTER

    private val rolePredictor: ComposeTextRolePredictor = DefaultComposeTextRolePredictor()

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val entries = ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType == SourceType.COMPOSE }
            .filter { component -> component.type == ComponentTypes.COMPOSE_TEXT }
            .mapNotNull { component -> collectEntry(component) }

        return entries
            .groupBy { entry -> entry.fileRoleKey }
            .flatMap { (_, roleEntries) -> analyzeRole(roleEntries) }
    }

    private fun collectEntry(component: UiComponent): TextSizeEntry? {
        val value = DimensionUtils.parseSp(component.properties.textSize) ?: return null
        return TextSizeEntry(
            component = component,
            role = rolePredictor.predict(component),
            value = value
        )
    }

    private fun analyzeRole(entries: List<TextSizeEntry>): List<AnalysisIssue> {
        return NearDuplicateDimensionAnalyzer.analyze(
            entries = entries,
            minDistinctValues = MIN_DISTINCT_VALUES,
            nearDuplicateDistance = NEAR_DUPLICATE_DISTANCE_SP,
            valueSelector = TextSizeEntry::value,
            resultFactory = ::createIssue
        )
    }

    private fun createIssue(entry: TextSizeEntry, canonicalValue: Float): AnalysisIssue {
        return AnalysisIssue(
            ruleId = id,
            severity = Severity.INFO,
            componentId = entry.component.id,
            componentLocator = entry.component.treePath?.let { path -> "${entry.component.type}[path=$path]" },
            componentType = entry.component.type,
            filePath = entry.component.filePath,
            message = AnalyzerMessages.composeTextSizeNearDuplicateCluster(
                value = entry.value,
                canonicalValue = canonicalValue,
                predictedRole = entry.role.name
            ),
            recommendation = AnalyzerMessages.composeTextSizeNearDuplicateClusterRecommendation(
                canonicalValue = canonicalValue,
                predictedRole = entry.role.name
            )
        )
    }

    private data class TextSizeEntry(
        val component: UiComponent,
        val role: ComposePredictedTextRole,
        val value: Float
    ) {
        val fileRoleKey: String
            get() = "${component.filePath}|${role.name}"
    }

    private companion object {
        const val MIN_DISTINCT_VALUES = 2
        const val NEAR_DUPLICATE_DISTANCE_SP = 1f
    }
}
