package ru.itis.report

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent
import java.io.File

class JsonReportGenerator : ReportGenerator {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }
    private val reportBuilder = AnalysisReportBuilder()

    override fun writeReport(
        outputFile: File,
        components: List<UiComponent>,
        issues: List<AnalysisIssue>
    ) {
        val report = reportBuilder.build(components, issues)

        outputFile.writeText(json.encodeToString(report))
    }
}

