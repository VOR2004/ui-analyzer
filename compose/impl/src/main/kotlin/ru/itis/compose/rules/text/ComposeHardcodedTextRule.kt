package ru.itis.compose.rules.text

import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeHardcodedTextRule : Rule {
    override val id: String = AnalyzerStrings.RuleIds.COMPOSE_HARDCODED_TEXT

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType == SourceType.COMPOSE }
            .filter { component -> component.type == ComponentTypes.COMPOSE_TEXT }
            .mapNotNull { component ->
                val text = component.properties.text ?: return@mapNotNull null

                AnalysisIssue(
                    ruleId = id,
                    severity = Severity.INFO,
                    componentId = component.id,
                    componentType = component.type,
                    filePath = component.filePath,
                    message = AnalyzerStrings.Messages.composeHardcodedText(text),
                    recommendation = AnalyzerStrings.Messages.COMPOSE_HARDCODED_TEXT_RECOMMENDATION
                )
            }
    }
}
