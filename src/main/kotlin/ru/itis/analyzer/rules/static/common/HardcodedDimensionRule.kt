package ru.itis.analyzer.rules.static.common

import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class HardcodedDimensionRule : Rule {

    override val id: String = AnalyzerStrings.RuleIds.HARDCODED_DIMENSION

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val flatComponents = ComponentUtils.flattenAll(components)
        val issues = mutableListOf<AnalysisIssue>()

        for (component in flatComponents) {
            issues += checkDimensionProperty(
                component = component,
                propertyName = AnalyzerStrings.PropertyNames.TEXT_SIZE,
                propertyValue = component.properties.textSize
            )

            issues += checkDimensionProperty(
                component = component,
                propertyName = AnalyzerStrings.PropertyNames.PADDING,
                propertyValue = component.properties.padding
            )

            issues += checkDimensionProperty(
                component = component,
                propertyName = AnalyzerStrings.PropertyNames.MARGIN,
                propertyValue = component.properties.margin
            )
        }

        return issues
    }

    private fun checkDimensionProperty(
        component: UiComponent,
        propertyName: String,
        propertyValue: String?,
    ): List<AnalysisIssue> {
        if (!isHardcodedDimension(propertyValue)) return emptyList()

        return listOf(
            AnalysisIssue(
                ruleId = id,
                severity = Severity.INFO,
                componentId = component.id,
                componentType = component.type,
                filePath = component.filePath,
                message = AnalyzerStrings.Messages.hardcodedDimension(propertyName, propertyValue),
                recommendation = AnalyzerStrings.Messages.HARDCODED_DIMENSION_RECOMMENDATION
            )
        )
    }

    private fun isHardcodedDimension(value: String?): Boolean {
        return DimensionUtils.isSp(value) || DimensionUtils.isDp(value)
    }
}
