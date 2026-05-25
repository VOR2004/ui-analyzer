package ru.itis.desktop.dialog

interface DesktopFileDialog {
    suspend fun selectProjectDirectory(): String?
    suspend fun selectRuntimeSnapshotFile(): String?
    suspend fun selectReportOutputFile(): String?
}
