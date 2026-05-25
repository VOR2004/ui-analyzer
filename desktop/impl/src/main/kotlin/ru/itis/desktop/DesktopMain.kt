package ru.itis.desktop

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.vinceglb.filekit.FileKit
import ru.itis.desktop.text.DesktopAppText
import ru.itis.desktop.ui.app.DesktopAnalyzerApp
import ru.itis.desktop.ui.window.installWindowResizeSupport
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Rectangle

fun main() = application {
    FileKit.init(appId = DesktopAppText.APP_ID)
    val windowState = rememberWindowState(width = 1280.dp, height = 820.dp)
    var dragStartPointer: Point? = null
    var dragStartWindowLocation: Point? = null
    var isMaximized by remember { mutableStateOf(false) }
    var restoreBounds by remember { mutableStateOf<Rectangle?>(null) }

    Window(
        onCloseRequest = ::exitApplication,
        title = DesktopAppText.WINDOW_TITLE,
        state = windowState,
        undecorated = true
    ) {
        LaunchedEffect(Unit) {
            window.minimumSize = Dimension(1220, 720)
        }
        DisposableEffect(window) {
            val uninstallResizeSupport = installWindowResizeSupport(window)
            onDispose { uninstallResizeSupport() }
        }
        DesktopAnalyzerApp(
            onClose = ::exitApplication,
            onMinimize = { windowState.isMinimized = true },
            onToggleMaximize = {
                if (isMaximized) {
                    restoreBounds?.let { bounds -> window.bounds = bounds }
                    isMaximized = false
                } else {
                    restoreBounds = window.bounds
                    window.bounds = GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .maximumWindowBounds
                    isMaximized = true
                }
            },
            onStartDragWindow = {
                if (isMaximized) return@DesktopAnalyzerApp
                dragStartPointer = MouseInfo.getPointerInfo().location
                dragStartWindowLocation = window.location
            },
            onDragWindow = {
                if (isMaximized) return@DesktopAnalyzerApp
                val startPointer = dragStartPointer ?: return@DesktopAnalyzerApp
                val startLocation = dragStartWindowLocation ?: return@DesktopAnalyzerApp
                val pointer = MouseInfo.getPointerInfo().location
                window.setLocation(
                    startLocation.x + pointer.x - startPointer.x,
                    startLocation.y + pointer.y - startPointer.y
                )
            },
            onStopDragWindow = {
                dragStartPointer = null
                dragStartWindowLocation = null
            },
            isMaximized = isMaximized
        )
    }
}
