package ru.itis.xml.rules.static.color
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.xml.helpers.ButtonColorAnalysisHelper
import ru.itis.xml.helpers.ButtonColorEntry
import ru.itis.analyzer.config.analyzer.AnalyzerThresholds
import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.rules.base.onlyXmlRoots
import ru.itis.analyzer.utils.ColorUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class XmlButtonColorProjectConsistencyRule(
    resourceRepository: ResourceRepository,
    private val nearThreshold: Double = AnalyzerThresholds.NEAR_COLOR_DISTANCE
) : Rule {

    override val id: String = RuleIds.BUTTON_COLOR_PROJECT_CONSISTENCY

    private val helper = ButtonColorAnalysisHelper(resourceRepository)

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val entries = helper.resolveButtonEntries(components.onlyXmlRoots())
        if (entries.size < 2) return emptyList()

        val nearDuplicateResult = helper.findNearDuplicateClusterResult(entries, nearThreshold)
        val dominantIssues = findDominantColorIssues(
            entries = entries,
            skipKeys = nearDuplicateResult.flaggedKeys
        )

        val nearDuplicateIssues = nearDuplicateResult.replacements.map { replacement ->
            AnalysisIssue(
                ruleId = RuleIds.nearDuplicateCluster(id),
                severity = Severity.INFO,
                componentId = replacement.entry.button.id,
                componentType = replacement.entry.button.type,
                filePath = replacement.entry.button.filePath,
                message = AnalyzerMessages.buttonColorProjectNearDuplicate(
                    color = replacement.entry.color,
                    canonicalColor = replacement.canonicalColor,
                    distance = helper.formatDistance(replacement.distance)
                ),
                recommendation = AnalyzerMessages.buttonColorProjectNearDuplicateRecommendation(
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
                    AnalyzerMessages.buttonColorProjectNearDominant(
                        color = entry.color,
                        dominantColor = dominantColor,
                        distance = helper.formatDistance(distance)
                    )
                } else {
                    AnalyzerMessages.buttonColorProjectDifferent(
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
                    recommendation = AnalyzerMessages.BUTTON_COLOR_PROJECT_RECOMMENDATION
                )
            }
    }
}


