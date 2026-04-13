package ru.itis.report

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent
import java.io.File

class JsonReportGenerator {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    fun writeReport(
        outputFile: File,
        components: List<UiComponent>,
        issues: List<AnalysisIssue>
    ) {
        val report = AnalysisReport(
            summary = Summary(
                totalComponents = components.sumOf { countComponents(it) },
                totalIssues = issues.size
            ),
            components = components,
            issues = issues
        )

        outputFile.writeText(json.encodeToString(report))
    }

    private fun countComponents(component: UiComponent): Int {
        return 1 + component.children.sumOf { countComponents(it) }
    }
}

@Serializable
data class AnalysisReport(
    val summary: Summary,
    val components: List<UiComponent>,
    val issues: List<AnalysisIssue>
)

@Serializable
data class Summary(
    val totalComponents: Int,
    val totalIssues: Int
)