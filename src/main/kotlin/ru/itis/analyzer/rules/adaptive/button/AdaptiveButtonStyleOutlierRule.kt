package ru.itis.analyzer.rules.adaptive.button

import kotlin.math.abs
import ru.itis.analyzer.core.AnalysisContext
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.utils.ColorUtils
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent
import ru.itis.style.signature.ButtonStyleSignature

class AdaptiveButtonStyleOutlierRule : ContextualRule {
    override val id: String = AnalyzerStrings.RuleIds.ADAPTIVE_BUTTON_STYLE_OUTLIER

    override fun check(components: List<UiComponent>): List<AnalysisIssue> = emptyList()

    override fun check(context: AnalysisContext): List<AnalysisIssue> {
        val issues = mutableListOf<AnalysisIssue>()
        val buttons = ComponentUtils.findButtons(context.components)

        for (button in buttons) {
            val screenProfile = context.screenProfiles[button.filePath] ?: continue
            val dominantStyle = screenProfile.dominantButtonStyle ?: continue
            val actualStyle = extractSignature(button) ?: continue
            val differences = collectDifferences(actualStyle, dominantStyle)

            if (differences.size >= MIN_DIFFERENCE_COUNT) {
                issues += AnalysisIssue(
                    ruleId = id,
                    severity = if (differences.size >= HIGH_DIFFERENCE_COUNT) Severity.WARNING else Severity.INFO,
                    componentId = button.id,
                    componentType = button.type,
                    filePath = button.filePath,
                    message = AnalyzerStrings.Messages.adaptiveButtonStyleOutlier(
                        differences = differences.joinToString(", ")
                    ),
                    recommendation = AnalyzerStrings.Messages.adaptiveButtonStyleOutlierRecommendation(
                        dominantStyle = formatStyle(dominantStyle)
                    )
                )
            }
        }

        return issues
    }

    private fun extractSignature(component: UiComponent): ButtonStyleSignature? {
        val signature = ButtonStyleSignature(
            background = ColorUtils.extractComparableColor(
                component.properties.backgroundTint ?: component.properties.backgroundColor
            ),
            textColor = ColorUtils.extractComparableColor(component.properties.textColor),
            textSize = DimensionUtils.parseSp(component.properties.textSize),
            padding = DimensionUtils.parseDp(component.properties.padding)
        )

        return signature.takeIf { it.isCompleteEnough() }
    }

    private fun collectDifferences(
        actual: ButtonStyleSignature,
        dominant: ButtonStyleSignature
    ): List<String> {
        val differences = mutableListOf<String>()

        if (!sameColor(actual.background, dominant.background)) {
            differences += AnalyzerStrings.PropertyNames.BACKGROUND
        }

        if (!sameColor(actual.textColor, dominant.textColor)) {
            differences += AnalyzerStrings.PropertyNames.TEXT_COLOR
        }

        if (!sameDimension(actual.textSize, dominant.textSize, TEXT_SIZE_TOLERANCE_SP)) {
            differences += AnalyzerStrings.PropertyNames.TEXT_SIZE
        }

        if (!sameDimension(actual.padding, dominant.padding, PADDING_TOLERANCE_DP)) {
            differences += AnalyzerStrings.PropertyNames.PADDING
        }

        return differences
    }

    private fun formatStyle(style: ButtonStyleSignature): String {
        return listOfNotNull(
            style.background?.let { "background=$it" },
            style.textColor?.let { "textColor=$it" },
            style.textSize?.let { "textSize=${it}sp" },
            style.padding?.let { "padding=${it}dp" }
        ).joinToString(", ")
    }

    private fun sameColor(first: String?, second: String?): Boolean {
        return first == second
    }

    private fun sameDimension(first: Float?, second: Float?, tolerance: Float): Boolean {
        if (first == null && second == null) {
            return true
        }
        if (first == null || second == null) {
            return false
        }

        return abs(first - second) <= tolerance
    }

    private companion object {
        const val MIN_DIFFERENCE_COUNT = 1
        const val HIGH_DIFFERENCE_COUNT = 2
        const val TEXT_SIZE_TOLERANCE_SP = 1f
        const val PADDING_TOLERANCE_DP = 2f
    }
}
