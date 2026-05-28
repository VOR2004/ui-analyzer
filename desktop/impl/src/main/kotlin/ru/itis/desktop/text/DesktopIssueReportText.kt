package ru.itis.desktop.text

import ru.itis.model.Severity

internal object DesktopIssueReportText {
    const val ISSUES = "Issues"
    const val EMPTY_REPORT = "Run analysis to inspect findings here."
    const val EMPTY_FILTER_RESULT = "No issues for selected filters."
    const val SELECT_ISSUE = "Select an issue to see details."
    const val ALL_SOURCES = "All"
    const val COMPOSE = "Compose"
    const val XML = "XML"
    const val RUNTIME = "Runtime"
    const val PROJECT = "Project"
    const val ALL_RULES = "All rules"
    const val ALL_SEVERITIES = "All severities"
    const val SOURCE = "Source"
    const val COMPONENT = "Component"
    const val COMPONENT_ID = "Component id"
    const val COMPOSABLE = "Composable"
    const val VISUAL_SOURCE = "Visual source"
    const val LOCATOR = "Locator"
    const val FILE = "File"
    const val MESSAGE = "Message"
    const val RECOMMENDATION = "Recommendation"
    const val NOT_AVAILABLE = "not available"

    fun showingIssues(visible: Int, total: Int): String {
        return "Showing $visible of $total"
    }

    fun severity(severity: Severity): String {
        return severity.name
    }
}
