package ru.itis.desktop.ui.app

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
import ru.itis.desktop.ui.screen.AnalyzerScreen
import ru.itis.desktop.ui.theme.analyzerColorScheme

@Composable
fun DesktopAnalyzerApp() {
    var theme by remember { mutableStateOf(DesktopTheme.DARK) }

    MaterialTheme(colorScheme = analyzerColorScheme(theme)) {
        Surface(modifier = Modifier.fillMaxSize()) {
            AnalyzerScreen(
                theme = theme,
                onThemeChange = { value -> theme = value }
            )
        }
    }
}
