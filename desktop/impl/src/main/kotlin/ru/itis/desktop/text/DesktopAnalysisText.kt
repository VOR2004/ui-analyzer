package ru.itis.desktop.text

import ru.itis.desktop.analysis.DesktopAnalysisResult

internal object DesktopAnalysisText {
    const val SELECT_RULE_ERROR = "Select at least one rule before analysis."
    const val READY_STATUS = "Ready for analysis."
    const val STARTED_STATUS = "Analysis started..."

    fun missingProjectDirectory(path: String): String {
        return "Android project directory does not exist: $path"
    }

    fun missingRuntimeSnapshot(path: String): String {
        return "Runtime snapshot file does not exist: $path"
    }

    fun success(result: DesktopAnalysisResult): String {
        return "Done: components=${result.componentCount}, issues=${result.issueCount}, report=${result.outputPath}"
    }

    fun failure(error: Throwable): String {
        return "Error: ${error.message}"
    }
}
