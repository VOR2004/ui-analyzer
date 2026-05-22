package ru.itis.xml.rules.static.common
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.messages.ui.UiPropertyNames

import ru.itis.analyzer.config.components.ResourcePatterns
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.rules.base.onlyXmlFlatComponents
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class XmlHardcodedDimensionRule : Rule {

    override val id: String = RuleIds.HARDCODED_DIMENSION

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val flatComponents = components.onlyXmlFlatComponents()
        val issues = mutableListOf<AnalysisIssue>()

        for (component in flatComponents) {
            issues += checkDimensionProperty(
                component = component,
                propertyName = UiPropertyNames.WIDTH,
                propertyValue = component.properties.width
            )

            issues += checkDimensionProperty(
                component = component,
                propertyName = UiPropertyNames.HEIGHT,
                propertyValue = component.properties.height
            )

            issues += checkDimensionProperty(
                component = component,
                propertyName = UiPropertyNames.TEXT_SIZE,
                propertyValue = component.properties.textSize
            )

            issues += checkDimensionProperty(
                component = component,
                propertyName = UiPropertyNames.PADDING,
                propertyValue = component.properties.padding
            )

            issues += checkDimensionProperty(
                component = component,
                propertyName = UiPropertyNames.MARGIN,
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
                message = AnalyzerMessages.hardcodedDimension(propertyName, propertyValue),
                recommendation = AnalyzerMessages.HARDCODED_DIMENSION_RECOMMENDATION
            )
        )
    }

    private fun isHardcodedDimension(value: String?): Boolean {
        val normalized = value?.trim() ?: return false
        if (isResourceReference(normalized)) {
            return false
        }

        return DimensionUtils.isSp(normalized) || DimensionUtils.isDp(normalized)
    }

    private fun isResourceReference(value: String): Boolean {
        return value.startsWith(ResourcePatterns.DIMEN_REF_PREFIX) ||
            value.startsWith(ResourcePatterns.ATTR_REF_PREFIX) ||
            value.startsWith(ResourcePatterns.ANDROID_ATTR_REF_PREFIX)
    }
}


