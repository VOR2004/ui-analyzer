package ru.itis.desktop.ui.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.itis.desktop.analysis.DesktopTheme
import ru.itis.desktop.ui.component.CustomTitleBar
import ru.itis.desktop.ui.screen.AnalyzerScreen
import ru.itis.desktop.ui.theme.analyzerColorScheme

@Composable
fun DesktopAnalyzerApp(
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onToggleMaximize: () -> Unit,
    onStartDragWindow: () -> Unit,
    onDragWindow: () -> Unit,
    onStopDragWindow: () -> Unit,
    isMaximized: Boolean
) {
    var theme by remember { mutableStateOf(DesktopTheme.DARK) }
    var titleBarActions by remember { mutableStateOf(DesktopTitleBarActions()) }

    MaterialTheme(colorScheme = analyzerColorScheme(theme)) {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomTitleBar(
                theme = theme,
                onThemeChange = { value -> theme = value },
                actions = titleBarActions,
                onMinimize = onMinimize,
                onToggleMaximize = onToggleMaximize,
                onClose = onClose,
                onStartDragWindow = onStartDragWindow,
                onDragWindow = onDragWindow,
                onStopDragWindow = onStopDragWindow,
                isMaximized = isMaximized
            )
            Surface(modifier = Modifier.weight(1f)) {
                AnalyzerScreen(
                    onTitleBarActionsChange = { value -> titleBarActions = value }
                )
            }
        }
    }
}
