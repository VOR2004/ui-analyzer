package ru.itis.xml.rules.static.common
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class XmlMissingIdRule : Rule {

    override val id: String = RuleIds.MISSING_ID

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.flattenAll(components)
            .filter { shouldHaveId(it) }
            .filter { it.id.isNullOrBlank() }
            .map { component ->
                AnalysisIssue(
                    ruleId = id,
                    severity = Severity.INFO,
                    componentId = component.id,
                    componentType = component.type,
                    filePath = component.filePath,
                    message = AnalyzerMessages.missingId(component.type),
                    recommendation = AnalyzerMessages.MISSING_ID_RECOMMENDATION
                )
            }
    }

    private fun shouldHaveId(component: UiComponent): Boolean {
        if (component.sourceType != SourceType.XML) {
            return false
        }

        return when (component.type) {
            ComponentTypes.LINEAR_LAYOUT,
            ComponentTypes.CONSTRAINT_LAYOUT,
            ComponentTypes.FRAME_LAYOUT,
            ComponentTypes.RELATIVE_LAYOUT,
            ComponentTypes.SCROLL_VIEW,
            ComponentTypes.HORIZONTAL_SCROLL_VIEW,
            ComponentTypes.ANDROIDX_CONSTRAINT_LAYOUT -> false

            else -> true
        }
    }
}


