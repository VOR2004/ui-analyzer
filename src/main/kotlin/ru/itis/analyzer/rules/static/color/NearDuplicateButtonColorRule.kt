package ru.itis.analyzer.rules.static.color

import ru.itis.analyzer.config.AnalyzerFormat
import ru.itis.analyzer.config.AnalyzerThresholds
import ru.itis.analyzer.helpers.ButtonColorAnalysisHelper
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.resource.ResourceRepository
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ColorUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent
import kotlin.collections.plusAssign

class NearDuplicateButtonColorRule(
    private val resourceRepository: ResourceRepository = ResourceRepository.empty(),
    private val threshold: Double = AnalyzerThresholds.NEAR_COLOR_DISTANCE
) : Rule {

    override val id: String = AnalyzerStrings.RuleIds.NEAR_DUPLICATE_BUTTON_COLORS
    private val helper = ButtonColorAnalysisHelper(resourceRepository)

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val buttonsWithColors = helper.resolveButtonEntries(components)
            .map { entry -> entry.button to entry.color }

        if (buttonsWithColors.size < 2) return emptyList()

        val issues = mutableListOf<AnalysisIssue>()
        val seenPairs = mutableSetOf<String>()

        for (i in buttonsWithColors.indices) {
            for (j in i + 1 until buttonsWithColors.size) {
                val first = buttonsWithColors[i]
                val second = buttonsWithColors[j]

                val firstButton = first.first
                val firstColor = first.second
                val secondButton = second.first
                val secondColor = second.second

                if (firstColor == secondColor) continue

                val distance = ColorUtils.colorDistance(firstColor, secondColor) ?: continue
                if (distance > threshold) continue

                val pairKey = buildPairKey(firstButton, secondButton, firstColor, secondColor)
                if (!seenPairs.add(pairKey)) continue

                issues += AnalysisIssue(
                    ruleId = id,
                    severity = Severity.INFO,
                    componentId = firstButton.id,
                    componentType = firstButton.type,
                    filePath = firstButton.filePath,
                    message = AnalyzerStrings.Messages.nearDuplicateButtonColors(firstColor, secondColor, distance),
                    recommendation = AnalyzerStrings.Messages.NEAR_DUPLICATE_BUTTON_COLORS_RECOMMENDATION
                )
            }
        }

        return issues
    }

    private fun buildPairKey(
        first: UiComponent,
        second: UiComponent,
        firstColor: String,
        secondColor: String
    ): String {
        val left = listOf(first.filePath, first.id, firstColor)
            .joinToString(AnalyzerFormat.ENTRY_KEY_DELIMITER)
        val right = listOf(second.filePath, second.id, secondColor)
            .joinToString(AnalyzerFormat.ENTRY_KEY_DELIMITER)
        return listOf(left, right).sorted().joinToString(AnalyzerFormat.PAIR_KEY_DELIMITER)
    }
}
