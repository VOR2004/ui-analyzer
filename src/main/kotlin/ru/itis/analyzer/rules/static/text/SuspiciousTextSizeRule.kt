package ru.itis.analyzer.rules.static.text

import ru.itis.analyzer.config.AnalyzerThresholds
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.source.xml.resource.ResourceRepository
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class SuspiciousTextSizeRule(
    private val resourceRepository: ResourceRepository = ResourceRepository.empty()
) : Rule {

    override val id: String = AnalyzerStrings.RuleIds.SUSPICIOUS_TEXT_SIZE

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
                    message = AnalyzerStrings.Messages.suspiciousTextSizeTooSmall(resolvedTextSize),
                    recommendation = AnalyzerStrings.Messages.SUSPICIOUS_TEXT_SIZE_TOO_SMALL_RECOMMENDATION
                )

                textSizeValue > AnalyzerThresholds.MAX_SUSPICIOUS_TEXT_SIZE_SP -> AnalysisIssue(
                    ruleId = id,
                    severity = Severity.WARNING,
                    componentId = component.id,
                    componentType = component.type,
                    filePath = component.filePath,
                    message = AnalyzerStrings.Messages.suspiciousTextSizeTooLarge(resolvedTextSize),
                    recommendation = AnalyzerStrings.Messages.SUSPICIOUS_TEXT_SIZE_TOO_LARGE_RECOMMENDATION
                )

                else -> null
            }
        }
    }

    private fun resolveDimension(value: String): String {
        return resourceRepository.resolveDimension(value) ?: value
    }
}
