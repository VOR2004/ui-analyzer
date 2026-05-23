package ru.itis.compose.rules.layout

import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.messages.ui.UiPropertyNames
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.analyzer.utils.NearDuplicateDimensionAnalyzer
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeNearDuplicateSpacingClusterRule : Rule {
    override val id: String = RuleIds.COMPOSE_NEAR_DUPLICATE_SPACING_CLUSTER

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val entries = ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType == SourceType.COMPOSE }
            .flatMap { component -> collectSpacingEntries(component) }

        return entries
            .groupBy { entry -> entry.component.filePath }
            .flatMap { (_, fileEntries) -> analyzeFile(fileEntries) }
    }

    private fun analyzeFile(entries: List<SpacingEntry>): List<AnalysisIssue> {
        return NearDuplicateDimensionAnalyzer.analyze(
            entries = entries,
            minDistinctValues = MIN_DISTINCT_VALUES,
            nearDuplicateDistance = NEAR_DUPLICATE_DISTANCE_DP,
            valueSelector = SpacingEntry::value,
            resultFactory = ::createIssue
        )
    }

    private fun collectSpacingEntries(component: UiComponent): List<SpacingEntry> {
        return listOfNotNull(
            parseEntry(component, UiPropertyNames.PADDING, component.properties.padding),
            parseEntry(component, UiPropertyNames.WIDTH, component.properties.width),
            parseEntry(component, UiPropertyNames.HEIGHT, component.properties.height)
        )
    }

    private fun parseEntry(
        component: UiComponent,
        propertyName: String,
        rawValue: String?
    ): SpacingEntry? {
        val value = DimensionUtils.parseDp(rawValue) ?: return null
        return SpacingEntry(component = component, propertyName = propertyName, value = value)
    }

    private fun createIssue(entry: SpacingEntry, canonicalValue: Float): AnalysisIssue {
        return AnalysisIssue(
            ruleId = id,
            severity = Severity.INFO,
            componentId = entry.component.id,
            componentLocator = entry.component.treePath?.let { path -> "${entry.component.type}[path=$path]" },
            componentType = entry.component.type,
            filePath = entry.component.filePath,
            message = AnalyzerMessages.composeNearDuplicateSpacingCluster(
                propertyName = entry.propertyName,
                value = entry.value,
                canonicalValue = canonicalValue
            ),
            recommendation = AnalyzerMessages.composeNearDuplicateSpacingClusterRecommendation(
                canonicalValue = canonicalValue
            )
        )
    }

    private data class SpacingEntry(
        val component: UiComponent,
        val propertyName: String,
        val value: Float
    )

    private companion object {
        const val MIN_DISTINCT_VALUES = 2
        const val NEAR_DUPLICATE_DISTANCE_DP = 2f
    }
}

