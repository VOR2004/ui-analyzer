package ru.itis.xml.rules.adaptive.text
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.core.AnalysisContext
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent
import ru.itis.xml.style.extractor.TextStyleSignatureExtractor
import ru.itis.xml.style.signature.PredictedTextRole
import ru.itis.xml.style.signature.TextStyleSignature

class XmlTooManyTextStylesOnScreenRule : ContextualRule {
    override val id: String = RuleIds.TOO_MANY_TEXT_STYLES_ON_SCREEN

    override fun check(components: List<UiComponent>): List<AnalysisIssue> = emptyList()

    override fun check(context: AnalysisContext): List<AnalysisIssue> {
        return context.components.flatMap { root ->
            analyzeScreen(context, root)
        }
    }

    private fun analyzeScreen(
        context: AnalysisContext,
        root: UiComponent
    ): List<AnalysisIssue> {
        val signatureExtractor = TextStyleSignatureExtractor(context.resourceRepository)
        val textStylesByRole = ComponentUtils.findTextViews(listOf(root))
            .mapNotNull { component -> signatureExtractor.extract(component) }
            .groupBy { signature -> signature.role ?: PredictedTextRole.BODY }

        return textStylesByRole.mapNotNull { (role, styles) ->
            analyzeRole(root, role, styles)
        }
    }

    private fun analyzeRole(
        root: UiComponent,
        role: PredictedTextRole,
        styles: List<TextStyleSignature>
    ): AnalysisIssue? {
        if (styles.size < MIN_ELEMENTS_PER_ROLE) {
            return null
        }

        val styleCounts = styles.groupingBy { it }.eachCount()
        val dominantCount = styleCounts.maxOfOrNull { it.value } ?: return null
        val repeatedStyleCount = styleCounts.count { (_, count) -> count >= MIN_STYLE_FREQUENCY }
        val dominantShare = dominantCount.toDouble() / styles.size

        val isFragmented =
            repeatedStyleCount > MAX_REPEATED_STYLES_PER_ROLE ||
                dominantShare < MIN_DOMINANT_SHARE

        if (!isFragmented) {
            return null
        }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.INFO,
            componentId = root.id,
            componentType = root.type,
            filePath = root.filePath,
            message = AnalyzerMessages.tooManyTextStylesOnScreen(
                role = role.name,
                actualCount = styleCounts.size,
                dominantSharePercent = (dominantShare * 100).toInt()
            ),
            recommendation = AnalyzerMessages.tooManyTextStylesOnScreenRecommendation(
                role = role.name
            )
        )
    }

    private companion object {
        const val MIN_ELEMENTS_PER_ROLE = 3
        const val MIN_STYLE_FREQUENCY = 2
        const val MAX_REPEATED_STYLES_PER_ROLE = 2
        const val MIN_DOMINANT_SHARE = 0.6
    }
}


