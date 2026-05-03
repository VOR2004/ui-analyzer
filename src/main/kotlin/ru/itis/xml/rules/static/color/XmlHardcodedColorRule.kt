package ru.itis.xml.rules.static.color

import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.rules.base.onlyXmlFlatComponents
import ru.itis.analyzer.utils.ColorUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class XmlHardcodedColorRule : Rule {

    override val id: String = AnalyzerStrings.RuleIds.HARDCODED_COLOR

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val flatComponents = components.onlyXmlFlatComponents()
        val issues = mutableListOf<AnalysisIssue>()

        for (component in flatComponents) {
            issues += checkColorProperty(component, AnalyzerStrings.PropertyNames.BACKGROUND, component.properties.backgroundColor)
            issues += checkColorProperty(component, AnalyzerStrings.PropertyNames.BACKGROUND_TINT, component.properties.backgroundTint)
            issues += checkColorProperty(component, AnalyzerStrings.PropertyNames.TINT, component.properties.tint)
            issues += checkColorProperty(component, AnalyzerStrings.PropertyNames.TEXT_COLOR, component.properties.textColor)
        }

        return issues
    }

    private fun checkColorProperty(
        component: UiComponent,
        propertyName: String,
        propertyValue: String?
    ): List<AnalysisIssue> {
        val normalizedColor = ColorUtils.normalizeHexColor(propertyValue) ?: return emptyList()

        return listOf(
            AnalysisIssue(
                ruleId = id,
                severity = Severity.INFO,
                componentId = component.id,
                componentType = component.type,
                filePath = component.filePath,
                message = AnalyzerStrings.Messages.hardcodedColor(propertyName, normalizedColor),
                recommendation = AnalyzerStrings.Messages.HARDCODED_COLOR_RECOMMENDATION
            )
        )
    }
}
