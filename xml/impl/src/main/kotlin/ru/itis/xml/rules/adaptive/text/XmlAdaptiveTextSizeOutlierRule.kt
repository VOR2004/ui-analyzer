package ru.itis.xml.rules.adaptive.text

import kotlin.math.abs
import ru.itis.analyzer.core.AnalysisContext
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent
import ru.itis.style.cluster.DimensionCluster
import ru.itis.xml.style.signature.PredictedTextRole
import ru.itis.xml.style.signature.TextRolePredictor

class XmlAdaptiveTextSizeOutlierRule : ContextualRule {
    override val id: String = AnalyzerStrings.RuleIds.ADAPTIVE_TEXT_SIZE_OUTLIER
    private val textRolePredictor = TextRolePredictor()

    override fun check(components: List<UiComponent>): List<AnalysisIssue> = emptyList()

    override fun check(context: AnalysisContext): List<AnalysisIssue> {
        val issues = mutableListOf<AnalysisIssue>()
        val textViews = ComponentUtils.findTextViews(context.components)

        for (component in textViews) {
            val screenProfile = context.screenProfiles[component.filePath] ?: continue
            val textSize = DimensionUtils.parseSp(
                context.resourceRepository.resolveDimension(component.properties.textSize)
                    ?: component.properties.textSize
            ) ?: continue
            val text = context.resourceRepository.resolveString(component.properties.text)
                ?: component.properties.text
            val predictedRole = textRolePredictor.predict(
                textSize = textSize,
                text = text,
                textStyle = component.properties.textStyle
            )
            val roleClusters = predictedRole
                ?.let { role -> screenProfile.textSizeClustersByRole[role] }
                .orEmpty()
            val roleCommonValues = commonValues(roleClusters)
            val fallbackCommonValues = commonValues(screenProfile.textSizeClusters)
            val commonValues = roleCommonValues.ifEmpty { fallbackCommonValues }

            if (isOutlier(textSize, commonValues)) {
                issues += createIssue(
                    component = component,
                    actualValue = textSize,
                    expectedValues = commonValues,
                    predictedRole = predictedRole.takeIf { roleCommonValues.isNotEmpty() }
                )
            }
        }

        return issues
    }

    private fun isOutlier(value: Float, commonValues: List<Float>): Boolean {
        if (commonValues.isEmpty()) {
            return false
        }

        return commonValues.none { abs(it - value) <= TEXT_SIZE_TOLERANCE_SP }
    }

    private fun commonValues(clusters: List<DimensionCluster>): List<Float> {
        return clusters
            .filter { cluster -> cluster.frequency > MIN_COMMON_CLUSTER_FREQUENCY }
            .map { cluster -> cluster.representativeValue }
    }

    private fun createIssue(
        component: UiComponent,
        actualValue: Float,
        expectedValues: List<Float>,
        predictedRole: PredictedTextRole?
    ): AnalysisIssue {
        val expected = expectedValues.joinToString(", ") { "${it}sp" }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = component.id,
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerStrings.Messages.adaptiveTextSizeOutlier(
                actualValue = actualValue,
                predictedRole = predictedRole?.name
            ),
            recommendation = AnalyzerStrings.Messages.adaptiveTextSizeOutlierRecommendation(expected)
        )
    }

    private companion object {
        const val TEXT_SIZE_TOLERANCE_SP = 1f
        const val MIN_COMMON_CLUSTER_FREQUENCY = 1
    }
}
