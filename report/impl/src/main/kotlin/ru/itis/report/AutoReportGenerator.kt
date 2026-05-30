package ru.itis.report

import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent
import ru.itis.report.json.JsonReportGenerator
import ru.itis.report.markdown.MarkdownReportGenerator
import java.io.File

class AutoReportGenerator(
    private val jsonReportGenerator: ReportGenerator = JsonReportGenerator(),
    private val markdownReportGenerator: ReportGenerator = MarkdownReportGenerator()
) : ReportGenerator {

    override fun writeReport(
        outputFile: File,
        components: List<UiComponent>,
        issues: List<AnalysisIssue>
    ) {
        when (outputFile.extension.lowercase()) {
            MARKDOWN_EXTENSION,
            MARKDOWN_SHORT_EXTENSION -> markdownReportGenerator
            else -> jsonReportGenerator
        }.writeReport(outputFile, components, issues)
    }

    private companion object {
        const val MARKDOWN_EXTENSION = "markdown"
        const val MARKDOWN_SHORT_EXTENSION = "md"
    }
}
