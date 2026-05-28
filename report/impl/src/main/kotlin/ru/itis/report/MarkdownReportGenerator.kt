package ru.itis.report

import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent
import java.io.File

class MarkdownReportGenerator : ReportGenerator {

    private val reportBuilder = AnalysisReportBuilder()

    override fun writeReport(
        outputFile: File,
        components: List<UiComponent>,
        issues: List<AnalysisIssue>
    ) {
        val report = reportBuilder.build(components, issues)
        outputFile.writeText(render(report))
    }

    private fun render(report: AnalysisReport): String {
        return buildString {
            appendLine("# UI Analysis Report")
            appendLine()
            appendSummary(report)
            appendSeverityBreakdown(report.issues)
            appendTopRules(report.issues)
            appendTopFiles(report.issues)
            appendIssueNavigation(report.issues)
            appendIssueDetails(report.issues)
        }
    }

    private fun StringBuilder.appendSummary(report: AnalysisReport) {
        appendLine("## Summary")
        appendLine()
        appendLine("| Metric | Value |")
        appendLine("| --- | ---: |")
        appendLine("| Components parsed | ${report.summary.totalComponents} |")
        appendLine("| Issues found | ${report.summary.totalIssues} |")
        appendLine()
    }

    private fun StringBuilder.appendSeverityBreakdown(issues: List<AnalysisIssue>) {
        appendLine("## Severity Breakdown")
        appendLine()
        appendLine("| Severity | Count |")
        appendLine("| --- | ---: |")
        Severity.entries.forEach { severity ->
            appendLine("| ${severity.name} | ${issues.count { issue -> issue.severity == severity }} |")
        }
        appendLine()
    }

    private fun StringBuilder.appendTopRules(issues: List<AnalysisIssue>) {
        appendLine("## Most Frequent Rules")
        appendLine()
        appendCountTable(
            header = "Rule",
            values = issues.groupingBy { issue -> issue.ruleId }.eachCount(),
            emptyText = "No rule findings.",
            valueRenderer = { value -> value.escapeMarkdown() }
        )
    }

    private fun StringBuilder.appendTopFiles(issues: List<AnalysisIssue>) {
        appendLine("## Most Affected Files")
        appendLine()
        appendCountTable(
            header = "File",
            values = issues.groupingBy { issue -> issue.filePath }.eachCount(),
            emptyText = "No affected files.",
            valueRenderer = { value -> value.toMarkdownInlineCode() }
        )
    }

    private fun StringBuilder.appendCountTable(
        header: String,
        values: Map<String, Int>,
        emptyText: String,
        valueRenderer: (String) -> String
    ) {
        if (values.isEmpty()) {
            appendLine(emptyText)
            appendLine()
            return
        }

        appendLine("| $header | Count |")
        appendLine("| --- | ---: |")
        values.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { entry -> entry.value }.thenBy { entry -> entry.key })
            .take(MAX_TABLE_ROWS)
            .forEach { entry ->
                appendLine("| ${valueRenderer(entry.key)} | ${entry.value} |")
            }
        appendLine()
    }

    private fun StringBuilder.appendIssueDetails(issues: List<AnalysisIssue>) {
        appendLine("## Issue Details")
        appendLine()

        if (issues.isEmpty()) {
            appendLine("No issues found.")
            appendLine()
            return
        }

        issues.groupBy { issue -> issue.reportSection() }
            .toSortedMap(compareBy<ReportSection> { section -> section.rank }.thenBy { section -> section.title })
            .forEach { (section, sectionIssues) ->
                appendSectionDivider()
                appendLine("""<a id="${section.anchor}"></a>""")
                appendLine()
                appendLine("<details open>")
                appendLine("<summary><strong>${section.title.uppercase()} Issues (${sectionIssues.size})</strong></summary>")
                appendLine()

                sectionIssues.groupBy { issue -> issue.ruleId }
                    .entries
                    .sortedWith(
                        compareByDescending<Map.Entry<String, List<AnalysisIssue>>> { entry -> entry.value.size }
                            .thenBy { entry -> entry.key }
                    )
                    .forEach { (ruleId, ruleIssues) ->
                        appendLine("""<a id="${section.ruleAnchor(ruleId)}"></a>""")
                        appendLine()
                        appendLine("<details>")
                        appendLine("<summary><strong>Rule:</strong> <code>${ruleId.escapeMarkdown()}</code> - Findings: ${ruleIssues.size}</summary>")
                        appendLine()

                        ruleIssues.sortedWith(issueComparator)
                            .forEachIndexed { index, issue ->
                                appendIssue(index + 1, issue)
                            }

                        appendLine("</details>")
                        appendLine()
                    }

                appendLine("</details>")
                appendLine()
            }
    }

    private fun StringBuilder.appendSectionDivider() {
        appendLine("---")
        appendLine()
    }

    private fun StringBuilder.appendIssueNavigation(issues: List<AnalysisIssue>) {
        if (issues.isEmpty()) return

        appendLine("## Issue Navigation")
        appendLine()

        issues.groupBy { issue -> issue.reportSection() }
            .toSortedMap(compareBy<ReportSection> { section -> section.rank }.thenBy { section -> section.title })
            .forEach { (section, sectionIssues) ->
                appendLine("- [${section.title} Issues (${sectionIssues.size})](#${section.anchor})")
                sectionIssues.groupBy { issue -> issue.ruleId }
                    .entries
                    .sortedWith(
                        compareByDescending<Map.Entry<String, List<AnalysisIssue>>> { entry -> entry.value.size }
                            .thenBy { entry -> entry.key }
                    )
                    .forEach { (ruleId, ruleIssues) ->
                        appendLine("  - [${ruleId.escapeMarkdown()} (${ruleIssues.size})](#${section.ruleAnchor(ruleId)})")
                    }
            }
        appendLine()
    }

    private fun StringBuilder.appendIssue(
        index: Int,
        issue: AnalysisIssue
    ) {
        appendLine("##### ${index}. ${issue.severity.name}")
        appendLine()
        appendLine("- Component: `${issue.componentType}`")
        appendLine("- Component id: ${issue.componentId?.escapeMarkdown() ?: NOT_AVAILABLE}")
        issue.componentLocator?.extractLocatorDetail(COMPOSE_FUNCTION_LOCATOR_KEY)?.let { composable ->
            appendLine("- Composable: `${composable.escapeMarkdown()}`")
        }
        issue.componentLocator?.extractVisualSource()?.let { visualSource ->
            appendLine("- Visual source: `${visualSource.escapeMarkdown()}`")
        }
        appendLine("- Locator: ${issue.formatLocator()}")
        appendRuntimeFullLocatorIfNeeded(issue)
        appendLine("- File: ${issue.filePath.toMarkdownInlineCode()}")
        issue.message?.takeIf { message -> message.isNotBlank() }?.let { message ->
            appendFormattedMessage(issue, message)
        }
        appendLine("- Recommendation: ${issue.recommendation.escapeMarkdown()}")
        appendLine()
    }

    private fun StringBuilder.appendFormattedMessage(
        issue: AnalysisIssue,
        message: String
    ) {
        when (issue.ruleId) {
            COMPOSE_RUNTIME_OVERLAPPING_CLICKABLE_COMPONENTS_RULE_ID -> {
                appendRuntimeOverlapMessage(message)
            }
            RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS_RULE_ID -> {
                appendRuntimeDuplicateVisibleTextActionsMessage(message)
            }
            RUNTIME_SMALL_TOUCH_TARGET_RULE_ID -> {
                appendRuntimeSmallTouchTargetMessage(message)
            }
            RUNTIME_TEXT_TRUNCATION_RISK_RULE_ID -> {
                appendRuntimeTextTruncationRiskMessage(message)
            }
            else -> {
                appendLine("- Message: ${message.escapeMarkdown()}")
            }
        }
    }

    private fun StringBuilder.appendRuntimeOverlapMessage(message: String) {
        val components = message.extractRuntimeComponentBlocks()
        val first = components.getOrNull(0)
        val second = components.getOrNull(1)
        val overlapArea = OVERLAP_AREA_PATTERN.find(message)?.groupValues?.get(1)

        if (first == null || second == null || overlapArea == null) {
            appendLine("- Message: ${message.escapeMarkdown()}")
            return
        }

        appendLine("- Message: Runtime clickable components overlap.")
        appendLine("- First component:")
        appendRuntimeComponentDetails(first)
        appendLine("- Second component:")
        appendRuntimeComponentDetails(second)
        appendLine("- Overlap area: `${overlapArea.escapeMarkdown()}`")
    }

    private fun StringBuilder.appendRuntimeDuplicateVisibleTextActionsMessage(message: String) {
        val count = FIRST_NUMBER_PATTERN.find(message)?.groupValues?.get(1)
        val label = DUPLICATE_ACTIONS_LABEL_PATTERN.find(message)?.groupValues?.get(1)
        val components = message.extractRuntimeComponentBlocks()

        if (count == null || label == null || components.isEmpty()) {
            appendLine("- Message: ${message.escapeMarkdown()}")
            return
        }

        appendLine("- Message: Runtime screen has duplicate visible clickable actions.")
        appendLine("- Visible label: `${label.escapeMarkdown()}`")
        appendLine("- Duplicates: `$count`")
        appendLine("- Components:")
        components.forEachIndexed { index, component ->
            appendLine("  - ${index + 1}.")
            appendRuntimeComponentDetails(component, indent = "    ")
        }
    }

    private fun StringBuilder.appendRuntimeSmallTouchTargetMessage(message: String) {
        val component = message.extractRuntimeComponentBlocks().firstOrNull()
        val width = WIDTH_PATTERN.find(message)?.groupValues?.get(1)
        val height = HEIGHT_PATTERN.find(message)?.groupValues?.get(1)

        if (component == null || width == null || height == null) {
            appendLine("- Message: ${message.escapeMarkdown()}")
            return
        }

        appendLine("- Message: Runtime clickable component has a small touch target.")
        appendLine("- Component:")
        appendRuntimeComponentDetails(component)
        appendLine("- Actual size:")
        appendLine("  - Width: `${width.escapeMarkdown()}`")
        appendLine("  - Height: `${height.escapeMarkdown()}`")
    }

    private fun StringBuilder.appendRuntimeTextTruncationRiskMessage(message: String) {
        val component = message.extractRuntimeComponentBlocks().firstOrNull()
        val text = TEXT_PATTERN.find(message)?.groupValues?.get(1)
        val bounds = MESSAGE_BOUNDS_PATTERN.find(message)?.groupValues?.get(1)
        val estimatedWidth = ESTIMATED_TEXT_WIDTH_PATTERN.find(message)?.groupValues?.get(1)

        if (component == null || text == null || bounds == null || estimatedWidth == null) {
            appendLine("- Message: ${message.escapeMarkdown()}")
            return
        }

        appendLine("- Message: Runtime text may be visually truncated.")
        appendLine("- Component:")
        appendRuntimeComponentDetails(component)
        appendLine("- Text: `${text.escapeMarkdown()}`")
        appendLine("- Bounds: `${bounds.escapeMarkdown()}`")
        appendLine("- Estimated text width: `${estimatedWidth.escapeMarkdown()}`")
    }

    private fun StringBuilder.appendRuntimeComponentDetails(
        component: String,
        indent: String = "  "
    ) {
        val type = component.substringAfter("[")
            .substringBefore(",")
            .trim()
            .takeIf { value -> value.isNotBlank() }
        val id = ID_PATTERN.find(component)?.groupValues?.get(1)
        val bounds = BOUNDS_PATTERN.find(component)?.groupValues?.get(1)

        appendLine("${indent}- Type: `${type ?: UNKNOWN_VALUE}`")
        appendLine("${indent}- Id: ${id?.toMarkdownInlineCode() ?: NOT_AVAILABLE}")
        bounds?.let { value ->
            appendLine("${indent}- Bounds: `${value.escapeMarkdown()}`")
        }
    }

    private fun String.extractRuntimeComponentBlocks(): List<String> {
        val components = mutableListOf<String>()
        var startIndex: Int? = null
        var depth = 0

        forEachIndexed { index, char ->
            when (char) {
                '[' -> {
                    if (depth == 0) {
                        startIndex = index
                    }
                    depth++
                }
                ']' -> {
                    if (depth > 0) {
                        depth--
                        if (depth == 0) {
                            startIndex?.let { start -> components += substring(start, index + 1) }
                            startIndex = null
                        }
                    }
                }
            }
        }

        return components.filter { component -> component.contains(ID_MARKER) || component.contains(BOUNDS_MARKER) }
    }

    private fun StringBuilder.appendRuntimeFullLocatorIfNeeded(issue: AnalysisIssue) {
        val locator = issue.componentLocator ?: return
        if (issue.reportSection() != ReportSection.RUNTIME) return
        if (locator == locator.shortenRuntimeLocator()) return

        appendLine()
        appendLine("<details>")
        appendLine("<summary>Full locator</summary>")
        appendLine()
        appendLine("```text")
        appendLine(locator.toRuntimeLocatorTree())
        appendLine("```")
        appendLine()
        appendLine("</details>")
        appendLine()
    }

    private fun String.escapeMarkdown(): String {
        return replace("|", "\\|")
            .replace("\n", " ")
            .replace("\r", " ")
            .trim()
    }

    private fun String.toMarkdownInlineCode(): String {
        return "`${replace("`", "\\`").escapeMarkdown()}`"
    }

    private fun String.extractVisualSource(): String? {
        return extractLocatorDetail(VISUAL_SOURCE_LOCATOR_KEY)
            ?: extractLocatorDetail(IMAGE_VECTOR_LOCATOR_KEY)
            ?: extractLocatorDetail(PAINTER_LOCATOR_KEY)
    }

    private fun String.extractLocatorDetail(key: String): String? {
        val details = substringAfter('[', missingDelimiterValue = "")
            .substringBeforeLast(']', missingDelimiterValue = "")
        if (details.isBlank()) return null

        return details.split(LOCATOR_DETAIL_SEPARATOR)
            .map { detail -> detail.trim() }
            .firstOrNull { detail -> detail.startsWith("$key=") }
            ?.substringAfter('=')
            ?.takeIf { value -> value.isNotBlank() }
    }

    private fun AnalysisIssue.formatLocator(): String {
        val locator = componentLocator ?: return NOT_AVAILABLE
        return if (reportSection() == ReportSection.RUNTIME) {
            locator.shortenRuntimeLocator().escapeMarkdown()
        } else {
            locator.escapeMarkdown()
        }
    }

    private fun String.shortenRuntimeLocator(): String {
        val path = extractLocatorDetail(PATH_LOCATOR_KEY) ?: return this
        val shortPath = path.shortenRuntimePath()
        return replace("path=$path", "path=$shortPath")
    }

    private fun String.shortenRuntimePath(): String {
        val segments = split(PATH_SEPARATOR)
            .filter { segment -> segment.isNotBlank() }

        if (segments.size <= MAX_RUNTIME_PATH_SEGMENTS) return this

        return segments
            .takeLast(MAX_RUNTIME_PATH_SEGMENTS)
            .joinToString(
                separator = PATH_SEPARATOR.toString(),
                prefix = "$PATH_SEPARATOR$PATH_ELLIPSIS$PATH_SEPARATOR"
            )
    }

    private fun String.toRuntimeLocatorTree(): String {
        val type = substringBefore('[', missingDelimiterValue = this)
        val path = extractLocatorDetail(PATH_LOCATOR_KEY) ?: return this

        val header = "$type:"
        val tree = path.split(PATH_SEPARATOR)
            .filter { segment -> segment.isNotBlank() }
            .mapIndexed { index, segment ->
                "${TREE_INDENT.repeat(index + 1)}$TREE_BRANCH $segment"
            }

        return (listOf(header) + tree).joinToString(separator = "\n")
    }

    private fun AnalysisIssue.reportSection(): ReportSection {
        return when {
            ruleId.startsWith(COMPOSE_RUNTIME_RULE_PREFIX) -> ReportSection.RUNTIME
            ruleId.startsWith(RUNTIME_RULE_PREFIX) -> ReportSection.RUNTIME
            ruleId.startsWith(XML_RULE_PREFIX) -> ReportSection.XML
            ruleId.startsWith(COMPOSE_RULE_PREFIX) -> ReportSection.COMPOSE
            componentLocator?.extractLocatorDetail(COMPOSE_FUNCTION_LOCATOR_KEY) != null -> ReportSection.COMPOSE
            filePath.endsWith(XML_FILE_EXTENSION, ignoreCase = true) -> ReportSection.XML
            filePath.endsWith(KOTLIN_FILE_EXTENSION, ignoreCase = true) -> ReportSection.COMPOSE
            else -> ReportSection.PROJECT
        }
    }

    private enum class ReportSection(
        val title: String,
        val rank: Int
    ) {
        COMPOSE("Compose", 0),
        XML("XML", 1),
        RUNTIME("Runtime", 2),
        PROJECT("Project", 3)
    }

    private val ReportSection.anchor: String
        get() = "section-${name.lowercase()}"

    private fun ReportSection.ruleAnchor(ruleId: String): String {
        return "rule-${name.lowercase()}-${ruleId.toAnchorSlug()}"
    }

    private fun String.toAnchorSlug(): String {
        return lowercase()
            .map { char -> if (char.isLetterOrDigit()) char else '-' }
            .joinToString(separator = "")
            .replace(ANCHOR_DASH_SEQUENCE_PATTERN, "-")
            .trim('-')
    }

    private companion object {
        const val MAX_TABLE_ROWS = 10
        const val NOT_AVAILABLE = "_not available_"
        const val LOCATOR_DETAIL_SEPARATOR = ","
        const val COMPOSE_FUNCTION_LOCATOR_KEY = "composable"
        const val VISUAL_SOURCE_LOCATOR_KEY = "visualSource"
        const val IMAGE_VECTOR_LOCATOR_KEY = "imageVector"
        const val PAINTER_LOCATOR_KEY = "painter"
        const val PATH_LOCATOR_KEY = "path"
        const val COMPOSE_RULE_PREFIX = "compose-"
        const val COMPOSE_RUNTIME_RULE_PREFIX = "compose-runtime-"
        const val XML_RULE_PREFIX = "xml-"
        const val RUNTIME_RULE_PREFIX = "runtime-"
        const val XML_FILE_EXTENSION = ".xml"
        const val KOTLIN_FILE_EXTENSION = ".kt"
        const val MAX_RUNTIME_PATH_SEGMENTS = 4
        const val PATH_SEPARATOR = '/'
        const val PATH_ELLIPSIS = "..."
        const val TREE_BRANCH = "-"
        const val TREE_INDENT = "  "
        const val COMPOSE_RUNTIME_OVERLAPPING_CLICKABLE_COMPONENTS_RULE_ID =
            "compose-runtime-overlapping-clickable-components"
        const val RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS_RULE_ID =
            "runtime-duplicate-visible-text-actions"
        const val RUNTIME_SMALL_TOUCH_TARGET_RULE_ID = "runtime-small-touch-target"
        const val RUNTIME_TEXT_TRUNCATION_RISK_RULE_ID = "runtime-text-truncation-risk"
        const val UNKNOWN_VALUE = "unknown"
        const val ID_MARKER = "id="
        const val BOUNDS_MARKER = "bounds="
        val ANCHOR_DASH_SEQUENCE_PATTERN = Regex("-+")
        val OVERLAP_AREA_PATTERN = Regex("""overlapArea=([^.\s]+)""")
        val FIRST_NUMBER_PATTERN = Regex("""\b(\d+)\b""")
        val DUPLICATE_ACTIONS_LABEL_PATTERN = Regex(""""([^"]+)"""")
        val ID_PATTERN = Regex("""\bid=([^,\]]+)""")
        val BOUNDS_PATTERN = Regex("""bounds=\[([^]]+)]""")
        val WIDTH_PATTERN = Regex("""width=([^,.\s]+)""")
        val HEIGHT_PATTERN = Regex("""height=([^,.\s]+)""")
        val TEXT_PATTERN = Regex("""text="([^"]*)"""")
        val MESSAGE_BOUNDS_PATTERN = Regex("""bounds=(.+?),\s+estimatedTextWidth=""")
        val ESTIMATED_TEXT_WIDTH_PATTERN = Regex("""estimatedTextWidth=([^.\s]+)""")

        val severityRank = mapOf(
            Severity.ERROR to 0,
            Severity.WARNING to 1,
            Severity.INFO to 2
        )

        val issueComparator = compareBy<AnalysisIssue> { issue -> severityRank.getValue(issue.severity) }
            .thenBy { issue -> issue.filePath }
            .thenBy { issue -> issue.ruleId }
            .thenBy { issue -> issue.componentId.orEmpty() }
    }
}
