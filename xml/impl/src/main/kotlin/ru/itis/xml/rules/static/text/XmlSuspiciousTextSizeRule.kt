package ru.itis.xml.rules.static.text
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.config.analyzer.AnalyzerThresholds
import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class XmlSuspiciousTextSizeRule(
    private val resourceRepository: ResourceRepository = ResourceRepository.empty()
) : Rule {

    override val id: String = RuleIds.SUSPICIOUS_TEXT_SIZE

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val textViews = ComponentUtils.findTextViews(components)

        return textViews.mapNotNull { component ->
            val rawTextSize = component.properties.textSize ?: return@mapNotNull null
            val resolvedTextSize = resolveDimension(rawTextSize)
            val textSizeValue = DimensionUtils.parseSp(resolvedTextSize) ?: return@mapNotNull null

            when {
                textSizeValue < AnalyzerThresholds.MIN_SUSPICIOUS_TEXT_SIZE_SP -> AnalysisIssue(
                    ruleId = id,
                    severity = Severity.ERROR,
                    componentId = component.id,
                    componentType = component.type,
                    filePath = component.filePath,
                    message = AnalyzerMessages.suspiciousTextSizeTooSmall(resolvedTextSize),
                    recommendation = AnalyzerMessages.SUSPICIOUS_TEXT_SIZE_TOO_SMALL_RECOMMENDATION
                )

                textSizeValue > AnalyzerThresholds.MAX_SUSPICIOUS_TEXT_SIZE_SP -> AnalysisIssue(
                    ruleId = id,
                    severity = Severity.WARNING,
                    componentId = component.id,
                    componentType = component.type,
                    filePath = component.filePath,
                    message = AnalyzerMessages.suspiciousTextSizeTooLarge(resolvedTextSize),
                    recommendation = AnalyzerMessages.SUSPICIOUS_TEXT_SIZE_TOO_LARGE_RECOMMENDATION
                )

                else -> null
            }
        }
    }

    private fun resolveDimension(value: String): String {
        return resourceRepository.resolveDimension(value) ?: value
    }
}


