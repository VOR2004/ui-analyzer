package ru.itis.xml.rules.adaptive.text

import kotlin.math.abs
import ru.itis.analyzer.core.AnalysisContext
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent
import ru.itis.xml.style.extractor.TextStyleSignatureExtractor
import ru.itis.xml.style.signature.TextStyleSignature

class XmlAdaptiveTextStyleOutlierRule : ContextualRule {
    override val id: String = AnalyzerStrings.RuleIds.ADAPTIVE_TEXT_STYLE_OUTLIER

    override fun check(components: List<UiComponent>): List<AnalysisIssue> = emptyList()

    override fun check(context: AnalysisContext): List<AnalysisIssue> {
        val issues = mutableListOf<AnalysisIssue>()
        val textViews = ComponentUtils.findTextViews(context.components)
        val signatureExtractor = TextStyleSignatureExtractor(context.resourceRepository)

        for (component in textViews) {
            val screenProfile = context.screenProfiles[component.filePath] ?: continue
            val actualStyle = signatureExtractor.extract(component) ?: continue
            val dominantStyle = actualStyle.role
                ?.let { role -> screenProfile.dominantTextStylesByRole[role] }
                ?: screenProfile.dominantTextStyle
                ?: continue
            val differences = collectDifferences(actualStyle, dominantStyle)

            if (differences.isNotEmpty()) {
                issues += AnalysisIssue(
                    ruleId = id,
                    severity = if (differences.size >= HIGH_DIFFERENCE_COUNT) Severity.WARNING else Severity.INFO,
                    componentId = component.id,
                    componentType = component.type,
                    filePath = component.filePath,
                    message = AnalyzerStrings.Messages.adaptiveTextStyleOutlier(
                        differences = differences.joinToString(", ")
                    ),
                    recommendation = AnalyzerStrings.Messages.adaptiveTextStyleOutlierRecommendation(
                        dominantStyle = formatStyle(dominantStyle)
                    )
                )
            }
        }

        return issues
    }

    private fun collectDifferences(
        actual: TextStyleSignature,
        dominant: TextStyleSignature
    ): List<String> {
        val differences = mutableListOf<String>()

        if (!sameDimension(actual.textSize, dominant.textSize, TEXT_SIZE_TOLERANCE_SP)) {
            differences += AnalyzerStrings.PropertyNames.TEXT_SIZE
        }
        if (actual.textStyle != dominant.textStyle) {
            differences += "textStyle"
        }
        if (actual.fontFamily != dominant.fontFamily) {
            differences += "fontFamily"
        }

        return differences
    }

    private fun sameDimension(first: Float?, second: Float?, tolerance: Float): Boolean {
        if (first == null && second == null) return true
        if (first == null || second == null) return false
        return abs(first - second) <= tolerance
    }

    private fun formatStyle(style: TextStyleSignature): String {
        return listOfNotNull(
            style.role?.let { "predictedRole=$it" },
            style.textSize?.let { "textSize=${it}sp" },
            style.textStyle?.let { "textStyle=$it" },
            style.fontFamily?.let { "fontFamily=$it" }
        ).joinToString(", ")
    }

    private companion object {
        const val HIGH_DIFFERENCE_COUNT = 2
        const val TEXT_SIZE_TOLERANCE_SP = 1f
    }
}
