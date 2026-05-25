package ru.itis.desktop.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ru.itis.desktop.theme.DesktopTheme

@Composable
fun analyzerColorScheme(theme: DesktopTheme) = when (theme) {
    DesktopTheme.LIGHT -> lightColorScheme(
        primary = Color(0xFF365F91),
        background = Color(0xFFF4F5F7),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE9EDF3),
        onSurface = Color(0xFF1F2328),
        onSurfaceVariant = Color(0xFF5F6670),
        outlineVariant = Color(0xFFD5DAE2)
    )
    DesktopTheme.DARK -> darkColorScheme(
        primary = Color(0xFF8FB8FF),
        background = Color(0xFF1E1F22),
        surface = Color(0xFF2B2D30),
        surfaceVariant = Color(0xFF34373C),
        onSurface = Color(0xFFE8EAED),
        onSurfaceVariant = Color(0xFFB8BDC7),
        outlineVariant = Color(0xFF4E5259)
    )
}
