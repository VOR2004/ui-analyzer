package ru.itis.analyzer.rules.adaptive.text

import ru.itis.analyzer.core.AnalysisContext
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent
import ru.itis.style.signature.PredictedTextRole
import ru.itis.style.signature.TextRolePredictor
import ru.itis.style.signature.TextStyleSignature

class TooManyTextStylesOnScreenRule : ContextualRule {
    override val id: String = AnalyzerStrings.RuleIds.TOO_MANY_TEXT_STYLES_ON_SCREEN

    private val textRolePredictor = TextRolePredictor()

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
        val textStylesByRole = ComponentUtils.findTextViews(listOf(root))
            .mapNotNull { component -> extractSignature(context, component) }
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
            message = AnalyzerStrings.Messages.tooManyTextStylesOnScreen(
                role = role.name,
                actualCount = styleCounts.size,
                dominantSharePercent = (dominantShare * 100).toInt()
            ),
            recommendation = AnalyzerStrings.Messages.tooManyTextStylesOnScreenRecommendation(
                role = role.name
            )
        )
    }

    private fun extractSignature(
        context: AnalysisContext,
        component: UiComponent
    ): TextStyleSignature? {
        val textSize = DimensionUtils.parseSp(
            context.resourceRepository.resolveDimension(component.properties.textSize)
                ?: component.properties.textSize
        )
        val text = context.resourceRepository.resolveString(component.properties.text)
            ?: component.properties.text
        val signature = TextStyleSignature(
            role = textRolePredictor.predict(
                textSize = textSize,
                text = text,
                textStyle = component.properties.textStyle
            ),
            textSize = textSize,
            textStyle = component.properties.textStyle?.trim()?.ifBlank { null },
            fontFamily = component.properties.fontFamily?.trim()?.ifBlank { null }
        )

        return signature.takeIf { it.isCompleteEnough() }
    }

    private companion object {
        const val MIN_ELEMENTS_PER_ROLE = 3
        const val MIN_STYLE_FREQUENCY = 2
        const val MAX_REPEATED_STYLES_PER_ROLE = 2
        const val MIN_DOMINANT_SHARE = 0.6
    }
}
