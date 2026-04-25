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
        val enrichedIssues = enrichIssuesWithLocators(components, issues)

        val report = AnalysisReport(
            summary = Summary(
                totalComponents = components.sumOf { countComponents(it) },
                totalIssues = enrichedIssues.size
            ),
            components = components,
            issues = enrichedIssues
        )

        outputFile.writeText(json.encodeToString(report))
    }

    private fun enrichIssuesWithLocators(
        components: List<UiComponent>,
        issues: List<AnalysisIssue>
    ): List<AnalysisIssue> {
        return issues.map { issue ->
            if (issue.componentLocator != null) {
                issue
            } else {
                val component = findComponentForIssue(components, issue)
                issue.copy(componentLocator = component?.let { buildComponentLocator(it) })
            }
        }
    }

    private fun findComponentForIssue(
        components: List<UiComponent>,
        issue: AnalysisIssue
    ): UiComponent? {
        return components
            .flatMap { flatten(it) }
            .firstOrNull { component ->
                component.filePath == issue.filePath &&
                    component.type == issue.componentType &&
                    component.id == issue.componentId
            }
    }

    private fun buildComponentLocator(component: UiComponent): String {
        val details = listOfNotNull(
            component.id?.let { "id=$it" },
            component.treePath?.let { "path=$it" },
            component.properties.text?.let { "text=${it.take(MAX_LOCATOR_VALUE_LENGTH)}" },
            component.properties.textSize?.let { "textSize=$it" },
            component.properties.textStyle?.let { "textStyle=$it" },
            component.properties.contentDescription?.let {
                "contentDescription=${it.take(MAX_LOCATOR_VALUE_LENGTH)}"
            }
        )

        return if (details.isEmpty()) {
            component.type
        } else {
            "${component.type}[${details.joinToString(", ")}]"
        }
    }

    private fun flatten(component: UiComponent): List<UiComponent> {
        return listOf(component) + component.children.flatMap { flatten(it) }
    }

    private fun countComponents(component: UiComponent): Int {
        return 1 + component.children.sumOf { countComponents(it) }
    }

    private companion object {
        const val MAX_LOCATOR_VALUE_LENGTH = 40
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
