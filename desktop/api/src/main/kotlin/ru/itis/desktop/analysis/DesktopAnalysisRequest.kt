package ru.itis.desktop.analysis

data class DesktopAnalysisRequest(
    val projectPath: String,
    val outputPath: String,
    val mode: DesktopAnalysisMode,
    val staticTarget: StaticSourceTarget,
    val runtimeSource: RuntimeSnapshotSource,
    val runtimeSnapshotPath: String,
    val adbSerial: String?,
    val selectedRuleIds: Set<String>
)
