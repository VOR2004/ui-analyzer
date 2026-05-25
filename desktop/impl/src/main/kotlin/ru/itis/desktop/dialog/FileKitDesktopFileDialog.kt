package ru.itis.desktop.dialog

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver

class FileKitDesktopFileDialog : DesktopFileDialog {

    override suspend fun selectProjectDirectory(): String? {
        return FileKit.openDirectoryPicker()
            ?.absolutePath()
    }

    override suspend fun selectRuntimeSnapshotFile(): String? {
        return FileKit.openFilePicker(
            type = FileKitType.File(listOf("json"))
        )?.absolutePath()
    }

    override suspend fun selectReportOutputFile(): String? {
        return FileKit.openFileSaver(
            suggestedName = "analysis-report",
            defaultExtension = "json"
        )?.absolutePath()
    }
}
