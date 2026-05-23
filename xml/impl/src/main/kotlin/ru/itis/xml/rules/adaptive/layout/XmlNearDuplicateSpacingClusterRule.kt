package ru.itis.xml.rules.adaptive.layout
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.messages.ui.UiPropertyNames

import kotlin.math.abs
import ru.itis.analyzer.core.AnalysisContext
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.rules.base.onlyXmlFlatComponents
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class XmlNearDuplicateSpacingClusterRule : ContextualRule {
    override val id: String = RuleIds.XML_NEAR_DUPLICATE_SPACING_CLUSTER

    override fun check(components: List<UiComponent>): List<AnalysisIssue> = emptyList()

    override fun check(context: AnalysisContext): List<AnalysisIssue> {
        val entries = context.components
            .onlyXmlFlatComponents()
            .flatMap { component -> collectSpacingEntries(context, component) }

        return entries
            .groupBy { entry -> entry.component.filePath }
            .flatMap { (_, fileEntries) -> analyzeFile(fileEntries) }
    }

    private fun collectSpacingEntries(
        context: AnalysisContext,
        component: UiComponent
    ): List<SpacingEntry> {
        return listOfNotNull(
            parseEntry(context, component, UiPropertyNames.PADDING, component.properties.padding),
            parseEntry(context, component, UiPropertyNames.MARGIN, component.properties.margin)
        )
    }

    private fun parseEntry(
        context: AnalysisContext,
        component: UiComponent,
        propertyName: String,
        rawValue: String?
    ): SpacingEntry? {
        val resolved = context.resourceRepository.resolveDimension(rawValue) ?: rawValue
        val value = DimensionUtils.parseDp(resolved) ?: return null
        return SpacingEntry(component = component, propertyName = propertyName, value = value)
    }

    private fun analyzeFile(entries: List<SpacingEntry>): List<AnalysisIssue> {
        val values = entries.map { entry -> entry.value }.distinct().sorted()
        if (values.size < MIN_DISTINCT_VALUES) {
            return emptyList()
        }

        return buildNearDuplicateClusters(values).flatMap { cluster ->
            val canonicalValue = findCanonicalValue(cluster, entries)
            entries
                .filter { entry -> entry.value in cluster && entry.value != canonicalValue }
                .map { entry -> createIssue(entry, canonicalValue) }
        }
    }

    private fun buildNearDuplicateClusters(values: List<Float>): List<Set<Float>> {
        val clusters = mutableListOf<MutableSet<Float>>()

        for (value in values) {
            val cluster = clusters.firstOrNull { existingCluster ->
                existingCluster.any { existingValue -> isNearDuplicate(value, existingValue) }
            }

            if (cluster == null) {
                clusters += mutableSetOf(value)
            } else {
                cluster += value
            }
        }

        return clusters.filter { cluster -> cluster.size > 1 }
    }

    private fun isNearDuplicate(first: Float, second: Float): Boolean {
        val distance = abs(first - second)
        return distance > 0f && distance <= NEAR_DUPLICATE_DISTANCE_DP
    }

    private fun findCanonicalValue(cluster: Set<Float>, entries: List<SpacingEntry>): Float {
        return cluster.maxWith(
            compareBy<Float> { value -> entries.count { entry -> entry.value == value } }
                .thenBy { value -> value }
        )
    }

    private fun createIssue(entry: SpacingEntry, canonicalValue: Float): AnalysisIssue {
        return AnalysisIssue(
            ruleId = id,
            severity = Severity.INFO,
            componentId = entry.component.id,
            componentType = entry.component.type,
            filePath = entry.component.filePath,
            message = AnalyzerMessages.xmlNearDuplicateSpacingCluster(
                propertyName = entry.propertyName,
                value = entry.value,
                canonicalValue = canonicalValue
            ),
            recommendation = AnalyzerMessages.xmlNearDuplicateSpacingClusterRecommendation(
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


