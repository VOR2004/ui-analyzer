package ru.itis.report.markdown

import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent
import ru.itis.report.AnalysisReport
import ru.itis.report.AnalysisReportBuilder
import ru.itis.report.ReportGenerator
import ru.itis.report.markdown.config.ANCHOR_DASH_SEQUENCE_PATTERN
import ru.itis.report.markdown.config.BOUNDS_MARKER
import ru.itis.report.markdown.config.BOUNDS_PATTERN
import ru.itis.report.markdown.config.COMPOSE_FUNCTION_LOCATOR_KEY
import ru.itis.report.markdown.config.COMPOSE_RULE_PREFIX
import ru.itis.report.markdown.config.COMPOSE_RUNTIME_OVERLAPPING_CLICKABLE_COMPONENTS_RULE_ID
import ru.itis.report.markdown.config.COMPOSE_RUNTIME_RULE_PREFIX
import ru.itis.report.markdown.config.DUPLICATE_ACTIONS_LABEL_PATTERN
import ru.itis.report.markdown.config.ESTIMATED_TEXT_WIDTH_PATTERN
import ru.itis.report.markdown.config.FIRST_NUMBER_PATTERN
import ru.itis.report.markdown.config.HEIGHT_PATTERN
import ru.itis.report.markdown.config.ID_MARKER
import ru.itis.report.markdown.config.ID_PATTERN
import ru.itis.report.markdown.config.IMAGE_VECTOR_LOCATOR_KEY
import ru.itis.report.markdown.config.KOTLIN_FILE_EXTENSION
import ru.itis.report.markdown.config.LOCATOR_DETAIL_SEPARATOR
import ru.itis.report.markdown.config.MAX_RUNTIME_PATH_SEGMENTS
import ru.itis.report.markdown.config.MAX_TABLE_ROWS
import ru.itis.report.markdown.config.MESSAGE_BOUNDS_PATTERN
import ru.itis.report.markdown.config.NOT_AVAILABLE
import ru.itis.report.markdown.config.OVERLAP_AREA_PATTERN
import ru.itis.report.markdown.config.PAINTER_LOCATOR_KEY
import ru.itis.report.markdown.config.PATH_ELLIPSIS
import ru.itis.report.markdown.config.PATH_LOCATOR_KEY
import ru.itis.report.markdown.config.PATH_SEPARATOR
import ru.itis.report.markdown.config.RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS_RULE_ID
import ru.itis.report.markdown.config.RUNTIME_RULE_PREFIX
import ru.itis.report.markdown.config.RUNTIME_SMALL_TOUCH_TARGET_RULE_ID
import ru.itis.report.markdown.config.RUNTIME_TEXT_TRUNCATION_RISK_RULE_ID
import ru.itis.report.markdown.config.TEXT_PATTERN
import ru.itis.report.markdown.config.TREE_BRANCH
import ru.itis.report.markdown.config.TREE_INDENT
import ru.itis.report.markdown.config.UNKNOWN_VALUE
import ru.itis.report.markdown.config.VISUAL_SOURCE_LOCATOR_KEY
import ru.itis.report.markdown.config.WIDTH_PATTERN
import ru.itis.report.markdown.config.XML_FILE_EXTENSION
import ru.itis.report.markdown.config.XML_RULE_PREFIX
import ru.itis.report.markdown.config.issueComparator
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
        return MarkdownWriter().apply {
            heading(level = 1, text = "UI Analysis Report")
            appendSummary(report)
            appendSeverityBreakdown(report.issues)
            appendTopRules(report.issues)
            appendTopFiles(report.issues)
            appendIssueNavigation(report.issues)
            appendIssueDetails(report.issues)
        }.toString()
    }

    private fun MarkdownWriter.appendSummary(report: AnalysisReport) {
        heading(level = 2, text = "Summary")
        table(
            headers = listOf("Metric", "Value"),
            alignment = listOf("---", "---:"),
            rows = listOf(
                listOf("Components parsed", report.summary.totalComponents.toString()),
                listOf("Issues found", report.summary.totalIssues.toString())
            )
        )
    }

    private fun MarkdownWriter.appendSeverityBreakdown(issues: List<AnalysisIssue>) {
        heading(level = 2, text = "Severity Breakdown")
        table(
            headers = listOf("Severity", "Count"),
            alignment = listOf("---", "---:"),
            rows = Severity.entries.map { severity ->
                listOf(severity.name, issues.count { issue -> issue.severity == severity }.toString())
            }
        )
    }

    private fun MarkdownWriter.appendTopRules(issues: List<AnalysisIssue>) {
        heading(level = 2, text = "Most Frequent Rules")
        appendCountTable(
            header = "Rule",
            values = issues.groupingBy { issue -> issue.ruleId }.eachCount(),
            emptyText = "No rule findings.",
            valueRenderer = { value -> value.escapeMarkdown() }
        )
    }

    private fun MarkdownWriter.appendTopFiles(issues: List<AnalysisIssue>) {
        heading(level = 2, text = "Most Affected Files")
        appendCountTable(
            header = "File",
            values = issues.groupingBy { issue -> issue.filePath }.eachCount(),
            emptyText = "No affected files.",
            valueRenderer = { value -> value.toMarkdownInlineCode() }
        )
    }

    private fun MarkdownWriter.appendCountTable(
        header: String,
        values: Map<String, Int>,
        emptyText: String,
        valueRenderer: (String) -> String
    ) {
        if (values.isEmpty()) {
            line(emptyText)
            blankLine()
            return
        }

        table(
            headers = listOf(header, "Count"),
            alignment = listOf("---", "---:"),
            rows = values.entries
                .sortedWith(compareByDescending<Map.Entry<String, Int>> { entry -> entry.value }.thenBy { entry -> entry.key })
                .take(MAX_TABLE_ROWS)
                .map { entry -> listOf(valueRenderer(entry.key), entry.value.toString()) }
        )
    }

    private fun MarkdownWriter.appendIssueNavigation(issues: List<AnalysisIssue>) {
        if (issues.isEmpty()) return

        heading(level = 2, text = "Issue Navigation")
        issues.groupBy { issue -> issue.reportSection() }
            .toSortedMap(compareBy<ReportSection> { section -> section.rank }.thenBy { section -> section.title })
            .forEach { (section, sectionIssues) ->
                bullet("[${section.title} Issues (${sectionIssues.size})](#${section.anchor})")
                sectionIssues.groupBy { issue -> issue.ruleId }
                    .entries
                    .sortedWith(
                        compareByDescending<Map.Entry<String, List<AnalysisIssue>>> { entry -> entry.value.size }
                            .thenBy { entry -> entry.key }
                    )
                    .forEach { (ruleId, ruleIssues) ->
                        bullet("[${ruleId.escapeMarkdown()} (${ruleIssues.size})](#${section.ruleAnchor(ruleId)})", indent = 1)
                    }
            }
        blankLine()
    }

    private fun MarkdownWriter.appendIssueDetails(issues: List<AnalysisIssue>) {
        heading(level = 2, text = "Issue Details")

        if (issues.isEmpty()) {
            line("No issues found.")
            blankLine()
            return
        }

        issues.groupBy { issue -> issue.reportSection() }
            .toSortedMap(compareBy<ReportSection> { section -> section.rank }.thenBy { section -> section.title })
            .forEach { (section, sectionIssues) ->
                horizontalRule()
                anchor(section.anchor)
                details(
                    summary = "<strong>${section.title.uppercase()} Issues (${sectionIssues.size})</strong>",
                    open = true
                ) {
                    appendRuleGroups(section, sectionIssues)
                }
            }
    }

    private fun MarkdownWriter.appendRuleGroups(
        section: ReportSection,
        issues: List<AnalysisIssue>
    ) {
        issues.groupBy { issue -> issue.ruleId }
            .entries
            .sortedWith(
                compareByDescending<Map.Entry<String, List<AnalysisIssue>>> { entry -> entry.value.size }
                    .thenBy { entry -> entry.key }
            )
            .forEach { (ruleId, ruleIssues) ->
                anchor(section.ruleAnchor(ruleId))
                details(
                    summary = "<strong>Rule:</strong> <code>${ruleId.escapeMarkdown()}</code> - Findings: ${ruleIssues.size}"
                ) {
                    ruleIssues.sortedWith(issueComparator)
                        .forEachIndexed { index, issue ->
                            appendIssue(index + 1, issue)
                        }
                }
            }
    }

    private fun MarkdownWriter.appendIssue(
        index: Int,
        issue: AnalysisIssue
    ) {
        heading(level = 5, text = "${index}. ${issue.severity.name}")
        bullet("Component: `${issue.componentType}`")
        bullet("Component id: ${issue.componentId?.escapeMarkdown() ?: NOT_AVAILABLE}")
        issue.componentLocator?.extractLocatorDetail(COMPOSE_FUNCTION_LOCATOR_KEY)?.let { composable ->
            bullet("Composable: `${composable.escapeMarkdown()}`")
        }
        issue.componentLocator?.extractVisualSource()?.let { visualSource ->
            bullet("Visual source: `${visualSource.escapeMarkdown()}`")
        }
        bullet("Locator: ${issue.formatLocator()}")
        appendRuntimeFullLocatorIfNeeded(issue)
        bullet("File: ${issue.filePath.toMarkdownInlineCode()}")
        issue.message?.takeIf { message -> message.isNotBlank() }?.let { message ->
            appendFormattedMessage(issue, message)
        }
        bullet("Recommendation: ${issue.recommendation.escapeMarkdown()}")
        blankLine()
    }

    private fun MarkdownWriter.appendFormattedMessage(
        issue: AnalysisIssue,
        message: String
    ) {
        when (issue.ruleId) {
            COMPOSE_RUNTIME_OVERLAPPING_CLICKABLE_COMPONENTS_RULE_ID -> appendRuntimeOverlapMessage(message)
            RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS_RULE_ID -> appendRuntimeDuplicateVisibleTextActionsMessage(message)
            RUNTIME_SMALL_TOUCH_TARGET_RULE_ID -> appendRuntimeSmallTouchTargetMessage(message)
            RUNTIME_TEXT_TRUNCATION_RISK_RULE_ID -> appendRuntimeTextTruncationRiskMessage(message)
            else -> bullet("Message: ${message.escapeMarkdown()}")
        }
    }

    private fun MarkdownWriter.appendRuntimeOverlapMessage(message: String) {
        val components = message.extractRuntimeComponentBlocks()
        val first = components.getOrNull(0)
        val second = components.getOrNull(1)
        val overlapArea = OVERLAP_AREA_PATTERN.find(message)?.groupValues?.get(1)

        if (first == null || second == null || overlapArea == null) {
            bullet("Message: ${message.escapeMarkdown()}")
            return
        }

        bullet("Message: Runtime clickable components overlap.")
        bullet("First component:")
        appendRuntimeComponentDetails(first)
        bullet("Second component:")
        appendRuntimeComponentDetails(second)
        bullet("Overlap area: `${overlapArea.escapeMarkdown()}`")
    }

    private fun MarkdownWriter.appendRuntimeDuplicateVisibleTextActionsMessage(message: String) {
        val count = FIRST_NUMBER_PATTERN.find(message)?.groupValues?.get(1)
        val label = DUPLICATE_ACTIONS_LABEL_PATTERN.find(message)?.groupValues?.get(1)
        val components = message.extractRuntimeComponentBlocks()

        if (count == null || label == null || components.isEmpty()) {
            bullet("Message: ${message.escapeMarkdown()}")
            return
        }

        bullet("Message: Runtime screen has duplicate visible clickable actions.")
        bullet("Visible label: `${label.escapeMarkdown()}`")
        bullet("Duplicates: `$count`")
        bullet("Components:")
        components.forEachIndexed { index, component ->
            bullet("${index + 1}.", indent = 1)
            appendRuntimeComponentDetails(component, indent = 2)
        }
    }

    private fun MarkdownWriter.appendRuntimeSmallTouchTargetMessage(message: String) {
        val component = message.extractRuntimeComponentBlocks().firstOrNull()
        val width = WIDTH_PATTERN.find(message)?.groupValues?.get(1)
        val height = HEIGHT_PATTERN.find(message)?.groupValues?.get(1)

        if (component == null || width == null || height == null) {
            bullet("Message: ${message.escapeMarkdown()}")
            return
        }

        bullet("Message: Runtime clickable component has a small touch target.")
        bullet("Component:")
        appendRuntimeComponentDetails(component)
        bullet("Actual size:")
        bullet("Width: `${width.escapeMarkdown()}`", indent = 1)
        bullet("Height: `${height.escapeMarkdown()}`", indent = 1)
    }

    private fun MarkdownWriter.appendRuntimeTextTruncationRiskMessage(message: String) {
        val component = message.extractRuntimeComponentBlocks().firstOrNull()
        val text = TEXT_PATTERN.find(message)?.groupValues?.get(1)
        val bounds = MESSAGE_BOUNDS_PATTERN.find(message)?.groupValues?.get(1)
        val estimatedWidth = ESTIMATED_TEXT_WIDTH_PATTERN.find(message)?.groupValues?.get(1)

        if (component == null || text == null || bounds == null || estimatedWidth == null) {
            bullet("Message: ${message.escapeMarkdown()}")
            return
        }

        bullet("Message: Runtime text may be visually truncated.")
        bullet("Component:")
        appendRuntimeComponentDetails(component)
        bullet("Text: `${text.escapeMarkdown()}`")
        bullet("Bounds: `${bounds.escapeMarkdown()}`")
        bullet("Estimated text width: `${estimatedWidth.escapeMarkdown()}`")
    }

    private fun MarkdownWriter.appendRuntimeComponentDetails(
        component: String,
        indent: Int = 1
    ) {
        val type = component.substringAfter("[")
            .substringBefore(",")
            .trim()
            .takeIf { value -> value.isNotBlank() }
        val id = ID_PATTERN.find(component)?.groupValues?.get(1)
        val bounds = BOUNDS_PATTERN.find(component)?.groupValues?.get(1)

        bullet("Type: `${type ?: UNKNOWN_VALUE}`", indent = indent)
        bullet("Id: ${id?.toMarkdownInlineCode() ?: NOT_AVAILABLE}", indent = indent)
        bounds?.let { value ->
            bullet("Bounds: `${value.escapeMarkdown()}`", indent = indent)
        }
    }

    private fun MarkdownWriter.appendRuntimeFullLocatorIfNeeded(issue: AnalysisIssue) {
        val locator = issue.componentLocator ?: return
        if (issue.reportSection() != ReportSection.RUNTIME) return
        if (locator == locator.shortenRuntimeLocator()) return

        blankLine()
        details(summary = "Full locator") {
            codeBlock(language = "text", content = locator.toRuntimeLocatorTree())
        }
    }

    private fun String.extractRuntimeComponentBlocks(): List<String> {
        val components = mutableListOf<String>()
        var startIndex: Int? = null
        var depth = 0

        forEachIndexed { index, char ->
            when (char) {
                '[' -> {
                    if (depth == 0) startIndex = index
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
                prefix = "${PATH_SEPARATOR}${PATH_ELLIPSIS}${PATH_SEPARATOR}"
            )
    }

    private fun String.toRuntimeLocatorTree(): String {
        val type = substringBefore('[', missingDelimiterValue = this)
        val path = extractLocatorDetail(PATH_LOCATOR_KEY) ?: return this

        val header = "$type:"
        val tree = path.split(PATH_SEPARATOR)
            .filter { segment -> segment.isNotBlank() }
            .mapIndexed { index, segment ->
                "${TREE_INDENT.repeat(index + 1)}${TREE_BRANCH} $segment"
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
}