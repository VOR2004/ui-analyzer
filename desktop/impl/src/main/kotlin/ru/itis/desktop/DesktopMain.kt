package ru.itis.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.vinceglb.filekit.FileKit
import ru.itis.desktop.ui.app.DesktopAnalyzerApp
import java.awt.Dimension

fun main() = application {
    FileKit.init(appId = "ui-analyzer")
    val windowState = rememberWindowState(width = 1280.dp, height = 820.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "UI Analyzer",
        state = windowState
    ) {
        LaunchedEffect(Unit) {
            window.minimumSize = Dimension(1220, 720)
        }
        DesktopAnalyzerApp()
    }
}
