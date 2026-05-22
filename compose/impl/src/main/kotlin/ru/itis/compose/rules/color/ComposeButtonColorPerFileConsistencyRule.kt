package ru.itis.compose.rules.color

import ru.itis.analyzer.config.AnalyzerThresholds
import ru.itis.compose.helpers.ComposeButtonColorAnalysisHelper
import ru.itis.compose.helpers.ComposeButtonColorEntry
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ColorUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class ComposeButtonColorPerFileConsistencyRule(
    private val nearThreshold: Double = AnalyzerThresholds.NEAR_COLOR_DISTANCE
) : Rule {
    override val id: String = AnalyzerStrings.RuleIds.COMPOSE_BUTTON_COLOR_PER_FILE_CONSISTENCY

    private val helper = ComposeButtonColorAnalysisHelper()

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return helper.resolveButtonEntries(components)
            .groupBy { entry -> entry.button.filePath }
            .flatMap { (filePath, fileEntries) ->
                if (fileEntries.size < MIN_BUTTONS_PER_FILE) {
                    emptyList()
                } else {
                    analyzeFile(filePath, fileEntries)
                }
            }
            .distinctBy { issue ->
                listOf(issue.ruleId, issue.componentId, issue.componentLocator, issue.filePath, issue.message)
                    .joinToString("|")
            }
    }

    private fun analyzeFile(
        filePath: String,
        entries: List<ComposeButtonColorEntry>
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
                componentLocator = replacement.entry.button.treePath?.let { path ->
                    "${replacement.entry.button.type}[path=$path]"
                },
                componentType = replacement.entry.button.type,
                filePath = filePath,
                message = AnalyzerStrings.Messages.composeButtonColorPerFileNearDuplicate(
                    color = replacement.entry.color,
                    canonicalColor = replacement.canonicalColor,
                    distance = helper.formatDistance(replacement.distance)
                ),
                recommendation = AnalyzerStrings.Messages.composeButtonColorPerFileNearDuplicateRecommendation(
                    replacement.canonicalColor
                )
            )
        }

        return dominantIssues + nearDuplicateIssues
    }

    private fun findDominantColorIssues(
        filePath: String,
        entries: List<ComposeButtonColorEntry>,
        skipKeys: Set<String>
    ): List<AnalysisIssue> {
        val groupedByColor = entries.groupBy { entry -> entry.color }
        if (groupedByColor.size <= 1) {
            return emptyList()
        }

        val dominantColor = helper.findDominantColor(entries) ?: return emptyList()

        return entries
            .filter { entry -> entry.color != dominantColor }
            .filter { entry -> helper.entryKey(entry.button) !in skipKeys }
            .map { entry ->
                val distance = ColorUtils.colorDistance(entry.color, dominantColor)
                val severity = if (distance != null && distance <= nearThreshold) {
                    Severity.INFO
                } else {
                    Severity.WARNING
                }
                val message = if (distance != null && distance <= nearThreshold) {
                    AnalyzerStrings.Messages.composeButtonColorPerFileNearDominant(
                        color = entry.color,
                        dominantColor = dominantColor,
                        distance = helper.formatDistance(distance)
                    )
                } else {
                    AnalyzerStrings.Messages.composeButtonColorPerFileDifferent(
                        color = entry.color,
                        dominantColor = dominantColor
                    )
                }

                AnalysisIssue(
                    ruleId = id,
                    severity = severity,
                    componentId = entry.button.id,
                    componentLocator = entry.button.treePath?.let { path -> "${entry.button.type}[path=$path]" },
                    componentType = entry.button.type,
                    filePath = filePath,
                    message = message,
                    recommendation = AnalyzerStrings.Messages.COMPOSE_BUTTON_COLOR_PER_FILE_RECOMMENDATION
                )
            }
    }

    private companion object {
        const val MIN_BUTTONS_PER_FILE = 2
    }
}
