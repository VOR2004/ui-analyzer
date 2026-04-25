package ru.itis.analyzer.rules.static.text

import ru.itis.analyzer.config.ResourcePatterns
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class HardcodedTextRule: Rule {

    override val id: String = AnalyzerStrings.RuleIds.HARDCODED_TEXT

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
                    message = AnalyzerStrings.Messages.hardcodedText(component.type, text),
                    recommendation = AnalyzerStrings.Messages.HARDCODED_TEXT_RECOMMENDATION
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
