package ru.itis.compose.rules.text
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import kotlin.math.abs
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.compose.source.analyzer.ComposeTextRolePredictor
import ru.itis.compose.style.signature.ComposePredictedTextRole
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeTextSizeNearDuplicateClusterRule : Rule {
    override val id: String = RuleIds.COMPOSE_TEXT_SIZE_NEAR_DUPLICATE_CLUSTER

    private val rolePredictor = ComposeTextRolePredictor()

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
        return distance > 0f && distance <= NEAR_DUPLICATE_DISTANCE_SP
    }

    private fun findCanonicalValue(cluster: Set<Float>, entries: List<TextSizeEntry>): Float {
        return cluster.maxWith(
            compareBy<Float> { value -> entries.count { entry -> entry.value == value } }
                .thenBy { value -> value }
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


