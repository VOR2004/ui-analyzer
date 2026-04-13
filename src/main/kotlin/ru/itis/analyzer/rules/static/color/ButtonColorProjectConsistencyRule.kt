package ru.itis.analyzer.rules.static.color

import ru.itis.analyzer.helpers.ButtonColorAnalysisHelper
import ru.itis.analyzer.helpers.ButtonColorEntry
import ru.itis.analyzer.config.AnalyzerThresholds
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.resource.ResourceRepository
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ColorUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class ButtonColorProjectConsistencyRule(
    resourceRepository: ResourceRepository,
    private val nearThreshold: Double = AnalyzerThresholds.NEAR_COLOR_DISTANCE
) : Rule {

    override val id: String = AnalyzerStrings.RuleIds.BUTTON_COLOR_PROJECT_CONSISTENCY

    private val helper = ButtonColorAnalysisHelper(resourceRepository)

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val entries = helper.resolveButtonEntries(components)
        if (entries.size < 2) return emptyList()

        val nearDuplicateResult = helper.findNearDuplicateClusterResult(entries, nearThreshold)
        val dominantIssues = findDominantColorIssues(
            entries = entries,
            skipKeys = nearDuplicateResult.flaggedKeys
        )

        val nearDuplicateIssues = nearDuplicateResult.replacements.map { replacement ->
            AnalysisIssue(
                ruleId = AnalyzerStrings.RuleIds.nearDuplicateCluster(id),
                severity = Severity.INFO,
                componentId = replacement.entry.button.id,
                componentType = replacement.entry.button.type,
                filePath = replacement.entry.button.filePath,
                message = AnalyzerStrings.Messages.buttonColorProjectNearDuplicate(
                    color = replacement.entry.color,
                    canonicalColor = replacement.canonicalColor,
                    distance = helper.formatDistance(replacement.distance)
                ),
                recommendation = AnalyzerStrings.Messages.buttonColorProjectNearDuplicateRecommendation(
                    replacement.canonicalColor
                )
            )
        }

        return (dominantIssues + nearDuplicateIssues)
            .distinctBy { listOf(it.ruleId, it.componentId, it.filePath, it.message).joinToString("|") }
    }

    private fun findDominantColorIssues(
        entries: List<ButtonColorEntry>,
        skipKeys: Set<String>
    ): List<AnalysisIssue> {
        val groupedByColor = entries.groupBy { it.color }
        if (groupedByColor.size <= 1) return emptyList()

        val dominantColor = helper.findDominantColor(entries) ?: return emptyList()

        return entries
            .filter { it.color != dominantColor }
            .filter { helper.entryKey(it.button) !in skipKeys }
            .map { entry ->
                val distance = ColorUtils.colorDistance(entry.color, dominantColor)
                val severity = if (distance != null && distance <= nearThreshold) {
                    Severity.INFO
                } else {
                    Severity.WARNING
                }

                val message = if (distance != null && distance <= nearThreshold) {
                    AnalyzerStrings.Messages.buttonColorProjectNearDominant(
                        color = entry.color,
                        dominantColor = dominantColor,
                        distance = helper.formatDistance(distance)
                    )
                } else {
                    AnalyzerStrings.Messages.buttonColorProjectDifferent(
                        color = entry.color,
                        dominantColor = dominantColor
                    )
                }

                AnalysisIssue(
                    ruleId = id,
                    severity = severity,
                    componentId = entry.button.id,
                    componentType = entry.button.type,
                    filePath = entry.button.filePath,
                    message = message,
                    recommendation = AnalyzerStrings.Messages.BUTTON_COLOR_PROJECT_RECOMMENDATION
                )
            }
    }
}
