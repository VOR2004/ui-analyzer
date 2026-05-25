package ru.itis.desktop.dialog

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import ru.itis.desktop.text.DesktopDialogText

class FileKitDesktopFileDialog : DesktopFileDialog {

    override suspend fun selectProjectDirectory(): String? {
        return FileKit.openDirectoryPicker()
            ?.absolutePath()
    }

    override suspend fun selectRuntimeSnapshotFile(): String? {
        return FileKit.openFilePicker(
            type = FileKitType.File(DesktopDialogText.SNAPSHOT_EXTENSIONS)
        )?.absolutePath()
    }

    override suspend fun selectReportOutputFile(): String? {
        return FileKit.openFileSaver(
            suggestedName = DesktopDialogText.REPORT_SUGGESTED_NAME,
            defaultExtension = DesktopDialogText.JSON_EXTENSION
        )?.absolutePath()
    }
}
