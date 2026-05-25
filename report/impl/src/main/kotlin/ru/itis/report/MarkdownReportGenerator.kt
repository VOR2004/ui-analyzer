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
            emptyText = "No rule findings."
        )
    }

    private fun StringBuilder.appendTopFiles(issues: List<AnalysisIssue>) {
        appendLine("## Most Affected Files")
        appendLine()
        appendCountTable(
            header = "File",
            values = issues.groupingBy { issue -> issue.filePath }.eachCount(),
            emptyText = "No affected files."
        )
    }

    private fun StringBuilder.appendCountTable(
        header: String,
        values: Map<String, Int>,
        emptyText: String
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
                appendLine("| ${entry.key.escapeMarkdown()} | ${entry.value} |")
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

        issues.sortedWith(issueComparator)
            .forEachIndexed { index, issue ->
                appendLine("### ${index + 1}. ${issue.ruleId.escapeMarkdown()} (${issue.severity.name})")
                appendLine()
                appendLine("- Component: `${issue.componentType}`")
                appendLine("- Component id: ${issue.componentId?.escapeMarkdown() ?: NOT_AVAILABLE}")
                appendLine("- Locator: ${issue.componentLocator?.escapeMarkdown() ?: NOT_AVAILABLE}")
                appendLine("- File: `${issue.filePath}`")
                issue.message?.takeIf { message -> message.isNotBlank() }?.let { message ->
                    appendLine("- Message: ${message.escapeMarkdown()}")
                }
                appendLine("- Recommendation: ${issue.recommendation.escapeMarkdown()}")
                appendLine()
            }
    }

    private fun String.escapeMarkdown(): String {
        return replace("|", "\\|")
            .replace("\n", " ")
            .replace("\r", " ")
            .trim()
    }

    private companion object {
        const val MAX_TABLE_ROWS = 10
        const val NOT_AVAILABLE = "_not available_"

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
