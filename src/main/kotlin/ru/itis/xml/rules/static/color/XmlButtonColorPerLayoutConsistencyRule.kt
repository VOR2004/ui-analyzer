package ru.itis.xml.rules.static.color

import ru.itis.xml.helpers.ButtonColorAnalysisHelper
import ru.itis.xml.helpers.ButtonColorEntry
import ru.itis.analyzer.config.AnalyzerThresholds
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.rules.base.onlyXmlRoots
import ru.itis.analyzer.utils.ColorUtils
import ru.itis.analyzer.utils.GroupingUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class XmlButtonColorPerLayoutConsistencyRule(
    resourceRepository: ResourceRepository,
    private val nearThreshold: Double = AnalyzerThresholds.NEAR_COLOR_DISTANCE
) : Rule {

    override val id: String = AnalyzerStrings.RuleIds.BUTTON_COLOR_PER_LAYOUT_CONSISTENCY

    private val helper = ButtonColorAnalysisHelper(resourceRepository)

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val groupedByFile = GroupingUtils.groupByFile(components.onlyXmlRoots())
        val issues = mutableListOf<AnalysisIssue>()

        for ((filePath, fileComponents) in groupedByFile) {
            val entries = helper.resolveButtonEntriesFromFlatComponents(fileComponents)
            if (entries.size < 2) continue

            issues += findIssuesInsideSingleLayout(filePath, entries)
        }

        return issues.distinctBy {
            listOf(it.ruleId, it.componentId, it.filePath, it.message).joinToString("|")
        }
    }

    private fun findIssuesInsideSingleLayout(
        filePath: String,
        entries: List<ButtonColorEntry>
    ): List<AnalysisIssue> {
        val nearDuplicateResult = helper.findNearDuplicateClusterResult(entries, nearThreshold)

        val dominantIssues = findDominantColorIssues(
            filePath = filePath,
            entries = entries,
            skipKeys = nearDuplicateResult.flaggedKeys
        )

        val nearDuplicateIssues = nearDuplicateResult.replacements.map { replacement ->
            AnalysisIssue(
                ruleId = AnalyzerStrings.RuleIds.nearDuplicateCluster(id),
                severity = Severity.INFO,
                componentId = replacement.entry.button.id,
                componentType = replacement.entry.button.type,
                filePath = filePath,
                message = AnalyzerStrings.Messages.buttonColorPerLayoutNearDuplicate(
                    color = replacement.entry.color,
                    canonicalColor = replacement.canonicalColor,
                    distance = helper.formatDistance(replacement.distance)
                ),
                recommendation = AnalyzerStrings.Messages.buttonColorPerLayoutNearDuplicateRecommendation(
                    replacement.canonicalColor
                )
            )
        }

        return dominantIssues + nearDuplicateIssues
    }

    private fun findDominantColorIssues(
        filePath: String,
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
                    AnalyzerStrings.Messages.buttonColorPerLayoutNearDominant(
                        color = entry.color,
                        dominantColor = dominantColor,
                        distance = helper.formatDistance(distance)
                    )
                } else {
                    AnalyzerStrings.Messages.buttonColorPerLayoutDifferent(
                        color = entry.color,
                        dominantColor = dominantColor
                    )
                }

                AnalysisIssue(
                    ruleId = id,
                    severity = severity,
                    componentId = entry.button.id,
                    componentType = entry.button.type,
                    filePath = filePath,
                    message = message,
                    recommendation = AnalyzerStrings.Messages.BUTTON_COLOR_PER_LAYOUT_RECOMMENDATION
                )
            }
    }
}
