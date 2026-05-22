package ru.itis.compose.rules.text
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import java.lang.String.format
import java.util.Locale
import ru.itis.analyzer.config.analyzer.AnalyzerThresholds
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ContrastUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.compose.style.ComposeColorValueNormalizer
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeTextContrastRule : Rule {
    override val id: String = RuleIds.COMPOSE_TEXT_CONTRAST

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return components.flatMap { component ->
            analyzeNode(
                component = component,
                inheritedBackground = null
            )
        }
    }

    private fun analyzeNode(
        component: UiComponent,
        inheritedBackground: String?
    ): List<AnalysisIssue> {
        if (component.sourceType != SourceType.COMPOSE) {
            return emptyList()
        }

        val effectiveBackground = normalizeComparableColor(component.properties.backgroundColor)
            ?: inheritedBackground

        val currentIssues = if (component.type == ComponentTypes.COMPOSE_TEXT) {
            listOfNotNull(checkTextContrast(component, effectiveBackground))
        } else {
            emptyList()
        }

        return currentIssues + component.children.flatMap { child ->
            analyzeNode(
                component = child,
                inheritedBackground = effectiveBackground
            )
        }
    }

    private fun checkTextContrast(
        component: UiComponent,
        effectiveBackground: String?
    ): AnalysisIssue? {
        val backgroundColor = effectiveBackground ?: return null
        val textColor = normalizeComparableColor(component.properties.textColor) ?: return null
        val contrast = ContrastUtils.calculateContrastRatio(textColor, backgroundColor) ?: return null
        val minContrast = resolveMinContrast(component)

        if (contrast >= minContrast) {
            return null
        }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = component.id,
            componentLocator = component.treePath?.let { path -> "${component.type}[path=$path]" },
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerMessages.composeInsufficientTextContrast(
                textColor = textColor,
                backgroundColor = backgroundColor,
                ratio = formatRatio(contrast),
                minContrast = minContrast
            ),
            recommendation = AnalyzerMessages.COMPOSE_INSUFFICIENT_TEXT_CONTRAST_RECOMMENDATION
        )
    }

    private fun normalizeComparableColor(value: String?): String? {
        return ComposeColorValueNormalizer.normalize(
            value = value,
            includeThemeTokens = false
        )
    }

    private fun resolveMinContrast(component: UiComponent): Double {
        val textSize = DimensionUtils.parseSp(component.properties.textSize)
        return if (textSize != null && textSize >= AnalyzerThresholds.LARGE_TEXT_THRESHOLD_SP) {
            AnalyzerThresholds.LARGE_TEXT_MIN_CONTRAST
        } else {
            AnalyzerThresholds.NORMAL_TEXT_MIN_CONTRAST
        }
    }

    private fun formatRatio(value: Double): String {
        return format(Locale.US, "%.2f", value)
    }
}


