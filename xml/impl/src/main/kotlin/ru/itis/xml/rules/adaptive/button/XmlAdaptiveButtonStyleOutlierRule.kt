package ru.itis.xml.rules.adaptive.button
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.messages.ui.UiPropertyNames

import kotlin.math.abs
import ru.itis.analyzer.core.AnalysisContext
import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.utils.ColorUtils
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent
import ru.itis.xml.style.signature.ButtonStyleSignature

class XmlAdaptiveButtonStyleOutlierRule : ContextualRule {
    override val id: String = RuleIds.ADAPTIVE_BUTTON_STYLE_OUTLIER

    override fun check(components: List<UiComponent>): List<AnalysisIssue> = emptyList()

    override fun check(context: AnalysisContext): List<AnalysisIssue> {
        val issues = mutableListOf<AnalysisIssue>()
        val buttons = ComponentUtils.findButtons(context.components)

        for (button in buttons) {
            val screenProfile = context.screenProfiles[button.filePath] ?: continue
            val dominantStyle = screenProfile.dominantButtonStyle ?: continue
            val actualStyle = extractSignature(context.resourceRepository, button) ?: continue
            val differences = collectDifferences(actualStyle, dominantStyle)

            if (differences.size >= MIN_DIFFERENCE_COUNT) {
                issues += AnalysisIssue(
                    ruleId = id,
                    severity = if (differences.size >= HIGH_DIFFERENCE_COUNT) Severity.WARNING else Severity.INFO,
                    componentId = button.id,
                    componentType = button.type,
                    filePath = button.filePath,
                    message = AnalyzerMessages.adaptiveButtonStyleOutlier(
                        differences = differences.joinToString(", ")
                    ),
                    recommendation = AnalyzerMessages.adaptiveButtonStyleOutlierRecommendation(
                        dominantStyle = formatStyle(dominantStyle)
                    )
                )
            }
        }

        return issues
    }

    private fun extractSignature(
        resourceRepository: ResourceRepository,
        component: UiComponent
    ): ButtonStyleSignature? {
        val signature = ButtonStyleSignature(
            background = resolveColor(
                resourceRepository,
                component.properties.backgroundTint ?: component.properties.backgroundColor
            ),
            textColor = resolveColor(resourceRepository, component.properties.textColor),
            textSize = DimensionUtils.parseSp(
                resourceRepository.resolveDimension(component.properties.textSize)
                    ?: component.properties.textSize
            ),
            padding = DimensionUtils.parseDp(
                resourceRepository.resolveDimension(component.properties.padding)
                    ?: component.properties.padding
            )
        )

        return signature.takeIf { it.isCompleteEnough() }
    }

    private fun resolveColor(
        resourceRepository: ResourceRepository,
        value: String?
    ): String? {
        return resourceRepository.resolveColor(value) ?: ColorUtils.extractComparableColor(value)
    }

    private fun collectDifferences(
        actual: ButtonStyleSignature,
        dominant: ButtonStyleSignature
    ): List<String> {
        val differences = mutableListOf<String>()

        if (!sameColor(actual.background, dominant.background)) {
            differences += UiPropertyNames.BACKGROUND
        }

        if (!sameColor(actual.textColor, dominant.textColor)) {
            differences += UiPropertyNames.TEXT_COLOR
        }

        if (!sameDimension(actual.textSize, dominant.textSize, TEXT_SIZE_TOLERANCE_SP)) {
            differences += UiPropertyNames.TEXT_SIZE
        }

        if (!sameDimension(actual.padding, dominant.padding, PADDING_TOLERANCE_DP)) {
            differences += UiPropertyNames.PADDING
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


