package ru.itis.xml.rules.static.text
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.config.components.ResourcePatterns
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class XmlHardcodedTextRule: Rule {

    override val id: String = RuleIds.HARDCODED_TEXT

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.flattenAll(components)
            .mapNotNull { component ->
                if (component.sourceType != SourceType.XML) return@mapNotNull null

                val text = component.properties.text ?: return@mapNotNull null

                if (!isHardcodedText(text)) return@mapNotNull null

                AnalysisIssue(
                    ruleId = id,
                    severity = Severity.INFO,
                    componentId = component.id,
                    componentType = component.type,
                    filePath = component.filePath,
                    message = AnalyzerMessages.hardcodedText(component.type, text),
                    recommendation = AnalyzerMessages.HARDCODED_TEXT_RECOMMENDATION
                )
            }
    }

    private fun isHardcodedText(value: String): Boolean {
        val trimmed = value.trim()

        if (trimmed.isBlank()) return false
        if (trimmed.startsWith(ResourcePatterns.STRING_REF_PREFIX)) return false
        if (trimmed.startsWith(ResourcePatterns.ANDROID_STRING_REF_PREFIX)) return false
        if (trimmed.startsWith(ResourcePatterns.NULL_REF)) return false
        if (trimmed.startsWith(ResourcePatterns.ATTR_REF_PREFIX)) return false
        if (trimmed.startsWith(ResourcePatterns.ANDROID_ATTR_REF_PREFIX)) return false

        return true
    }
}


