package ru.itis.report

import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent
import java.io.File

interface ReportGenerator {

    fun writeReport(
        outputFile: File,
        components: List<UiComponent>,
        issues: List<AnalysisIssue>
    )
}
