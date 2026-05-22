package ru.itis.xml.rules.adaptive.layout

import kotlin.math.abs
import ru.itis.analyzer.core.AnalysisContext
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class XmlAdaptiveSpacingOutlierRule : ContextualRule {
    override val id: String = AnalyzerStrings.RuleIds.ADAPTIVE_SPACING_OUTLIER

    override fun check(components: List<UiComponent>): List<AnalysisIssue> = emptyList()

    override fun check(context: AnalysisContext): List<AnalysisIssue> {
        val issues = mutableListOf<AnalysisIssue>()
        val flat = ComponentUtils.flattenAll(context.components)

        for (component in flat) {
            val screenProfile = context.screenProfiles[component.filePath] ?: continue
            val commonSpacingValues = screenProfile.spacingScale.commonValuesDp

            if (commonSpacingValues.isEmpty()) {
                continue
            }

            val padding = DimensionUtils.parseDp(
                context.resourceRepository.resolveDimension(component.properties.padding)
                    ?: component.properties.padding
            )
            if (padding != null && isOutlier(padding, commonSpacingValues)) {
                issues += createIssue(
                    component = component,
                    propertyName = AnalyzerStrings.PropertyNames.PADDING,
                    actualValue = padding,
                    expectedValues = commonSpacingValues
                )
            }

            val margin = DimensionUtils.parseDp(
                context.resourceRepository.resolveDimension(component.properties.margin)
                    ?: component.properties.margin
            )
            if (margin != null && isOutlier(margin, commonSpacingValues)) {
                issues += createIssue(
                    component = component,
                    propertyName = AnalyzerStrings.PropertyNames.MARGIN,
                    actualValue = margin,
                    expectedValues = commonSpacingValues
                )
            }
        }

        return issues
    }

    private fun isOutlier(value: Float, commonValues: List<Float>): Boolean {
        if (commonValues.isEmpty()) {
            return false
        }

        return commonValues.none { abs(it - value) <= SPACING_TOLERANCE_DP }
    }

    private fun createIssue(
        component: UiComponent,
        propertyName: String,
        actualValue: Float,
        expectedValues: List<Float>
    ): AnalysisIssue {
        val expected = expectedValues.joinToString(", ") { "${it}dp" }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = component.id,
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerStrings.Messages.adaptiveSpacingOutlier(
                propertyName = propertyName,
                actualValue = actualValue
            ),
            recommendation = AnalyzerStrings.Messages.adaptiveSpacingOutlierRecommendation(expected)
        )
    }

    private companion object {
        const val SPACING_TOLERANCE_DP = 2f
    }
}
