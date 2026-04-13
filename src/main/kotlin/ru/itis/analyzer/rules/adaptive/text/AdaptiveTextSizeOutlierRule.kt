package ru.itis.analyzer.rules.adaptive.text

import kotlin.math.abs
import ru.itis.analyzer.core.AnalysisContext
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class AdaptiveTextSizeOutlierRule : ContextualRule {
    override val id: String = AnalyzerStrings.RuleIds.ADAPTIVE_TEXT_SIZE_OUTLIER

    override fun check(components: List<UiComponent>): List<AnalysisIssue> = emptyList()

    override fun check(context: AnalysisContext): List<AnalysisIssue> {
        val issues = mutableListOf<AnalysisIssue>()
        val textViews = ComponentUtils.findTextViews(context.components)

        for (component in textViews) {
            val screenProfile = context.screenProfiles[component.filePath] ?: continue
            val textSize = DimensionUtils.parseSp(component.properties.textSize) ?: continue
            val commonValues = screenProfile.textSizeClusters
                .filter { cluster -> cluster.frequency > MIN_COMMON_CLUSTER_FREQUENCY }
                .map { cluster -> cluster.representativeValue }

            if (isOutlier(textSize, commonValues)) {
                issues += createIssue(
                    component = component,
                    actualValue = textSize,
                    expectedValues = commonValues
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

    private fun createIssue(
        component: UiComponent,
        actualValue: Float,
        expectedValues: List<Float>
    ): AnalysisIssue {
        val expected = expectedValues.joinToString(", ") { "${it}sp" }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = component.id,
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerStrings.Messages.adaptiveTextSizeOutlier(actualValue),
            recommendation = AnalyzerStrings.Messages.adaptiveTextSizeOutlierRecommendation(expected)
        )
    }

    private companion object {
        const val TEXT_SIZE_TOLERANCE_SP = 1f
        const val MIN_COMMON_CLUSTER_FREQUENCY = 1
    }
}
