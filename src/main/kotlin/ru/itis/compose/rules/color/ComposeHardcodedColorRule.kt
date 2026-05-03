package ru.itis.compose.rules.color

import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeHardcodedColorRule : Rule {
    override val id: String = AnalyzerStrings.RuleIds.COMPOSE_HARDCODED_COLOR

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType == SourceType.COMPOSE }
            .flatMap { component ->
                listOfNotNull(
                    checkColorProperty(component, AnalyzerStrings.PropertyNames.BACKGROUND, component.properties.backgroundColor),
                    checkColorProperty(component, AnalyzerStrings.PropertyNames.BACKGROUND_TINT, component.properties.backgroundTint),
                    checkColorProperty(component, AnalyzerStrings.PropertyNames.TINT, component.properties.tint),
                    checkColorProperty(component, AnalyzerStrings.PropertyNames.TEXT_COLOR, component.properties.textColor)
                )
            }
    }

    private fun checkColorProperty(
        component: UiComponent,
        propertyName: String,
        propertyValue: String?
    ): AnalysisIssue? {
        val normalized = propertyValue?.trim() ?: return null
        if (!isHardcodedComposeColor(normalized)) {
            return null
        }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = component.id,
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerStrings.Messages.composeHardcodedColor(propertyName, normalized),
            recommendation = AnalyzerStrings.Messages.COMPOSE_HARDCODED_COLOR_RECOMMENDATION
        )
    }

    private fun isHardcodedComposeColor(value: String): Boolean {
        return value.startsWith("Color(") ||
            HEX_COLOR_LITERAL.matches(value)
    }

    private companion object {
        val HEX_COLOR_LITERAL = Regex("""(?:0x[0-9A-Fa-f]{8}|#[0-9A-Fa-f]{6,8})""")
    }
}
