package ru.itis.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit
import ru.itis.desktop.ui.app.DesktopAnalyzerApp

fun main() = application {
    FileKit.init(appId = "ui-analyzer")
    Window(
        onCloseRequest = ::exitApplication,
        title = "UI Analyzer"
    ) {
        DesktopAnalyzerApp()
    }
}
