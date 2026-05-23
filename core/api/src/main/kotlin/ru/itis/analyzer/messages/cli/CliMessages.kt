package ru.itis.analyzer.messages.cli

object CliMessages {
    const val USAGE =
        "Usage: ui-analyzer <path-to-android-project> [output-file] [--rules=all|static|xml|compose|runtime] [runtime-snapshot-json|--runtime-adb] [adb-serial]"
    const val DEFAULT_OUTPUT_PATH = "analysis-report.json"
    const val ANALYSIS_COMPLETE = "Analysis complete"

    fun projectDirectoryDoesNotExist(path: String): String =
        "Project directory does not exist: $path"

    fun foundLayoutXmlFiles(count: Int): String =
        "Found $count layout XML files"

    fun loadedRuntimeComponents(count: Int): String =
        "Loaded $count runtime components"

    fun capturingRuntimeWithAdb(serial: String?): String =
        "Capturing Android runtime snapshot via ADB${serial?.let { " ($it)" }.orEmpty()}"

    fun failedToParse(path: String, message: String?): String =
        "Failed to parse $path: $message"

    fun componentsParsed(count: Int): String =
        "Components parsed: $count"

    fun issuesFound(count: Int): String =
        "Issues found: $count"

    fun reportWrittenTo(path: String): String =
        "Report written to: $path"
}