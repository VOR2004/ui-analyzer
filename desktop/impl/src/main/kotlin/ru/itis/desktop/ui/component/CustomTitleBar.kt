package ru.itis.desktop.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.itis.desktop.text.DesktopAppText
import ru.itis.desktop.text.DesktopTitleBarText
import ru.itis.desktop.theme.DesktopTheme
import ru.itis.desktop.ui.app.DesktopTitleBarActions

@Composable
fun CustomTitleBar(
    theme: DesktopTheme,
    onThemeChange: (DesktopTheme) -> Unit,
    actions: DesktopTitleBarActions,
    onMinimize: () -> Unit,
    onToggleMaximize: () -> Unit,
    onClose: () -> Unit,
    onStartDragWindow: () -> Unit,
    onDragWindow: () -> Unit,
    onStopDragWindow: () -> Unit,
    isMaximized: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onStartDragWindow() },
                    onDragEnd = { onStopDragWindow() },
                    onDragCancel = { onStopDragWindow() }
                ) { change, _ ->
                    change.consume()
                    onDragWindow()
                }
            }
            .padding(start = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = DesktopAppText.WINDOW_TITLE,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.width(18.dp))
        FileTitleBarMenu(
            onOpenProject = actions.onOpenProject,
            onSelectReportOutput = actions.onSelectReportOutput,
            onExit = onClose
        )
        RulesTitleBarMenu(
            rulesPanelVisible = actions.rulesPanelVisible,
            onToggleRules = actions.onToggleRules
        )
        RunTitleBarMenu(
            isRunning = actions.isRunning,
            onRunAnalysis = actions.onRunAnalysis
        )
        Spacer(modifier = Modifier.weight(1f))

        TitleBarIconButton(
            onClick = {
                onThemeChange(
                    if (theme == DesktopTheme.DARK) DesktopTheme.LIGHT else DesktopTheme.DARK
                )
            }
        ) { color ->
            ThemeIcon(color = color, showDarkAction = theme == DesktopTheme.LIGHT)
        }
        WindowControlButton(onClick = onMinimize) { color -> MinimizeIcon(color = color) }
        WindowControlButton(onClick = onToggleMaximize) { color ->
            if (isMaximized) {
                RestoreIcon(color = color)
            } else {
                MaximizeIcon(color = color)
            }
        }
        WindowControlButton(onClick = onClose) { color -> CloseIcon(color = color) }
    }
}

@Composable
private fun TitleBarMenuButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val textColor = if (enabled) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.48f)
    }
    Box(
        modifier = modifier
            .height(30.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun FileTitleBarMenu(
    onOpenProject: () -> Unit,
    onSelectReportOutput: () -> Unit,
    onExit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TitleBarMenuButton(
            text = DesktopTitleBarText.FILE,
            onClick = { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TitleBarDropdownItem(
                text = DesktopTitleBarText.OPEN_PROJECT,
                onClick = {
                    expanded = false
                    onOpenProject()
                }
            )
            TitleBarDropdownItem(
                text = DesktopTitleBarText.CHOOSE_REPORT_OUTPUT,
                onClick = {
                    expanded = false
                    onSelectReportOutput()
                }
            )
            TitleBarDropdownItem(
                text = DesktopTitleBarText.EXIT,
                onClick = {
                    expanded = false
                    onExit()
                }
            )
        }
    }
}

@Composable
private fun RulesTitleBarMenu(
    rulesPanelVisible: Boolean,
    onToggleRules: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TitleBarMenuButton(
            text = DesktopTitleBarText.RULES,
            onClick = { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TitleBarDropdownItem(
                text = if (rulesPanelVisible) DesktopTitleBarText.HIDE_RULES else DesktopTitleBarText.SHOW_RULES,
                onClick = {
                    expanded = false
                    onToggleRules()
                }
            )
        }
    }
}

@Composable
private fun RunTitleBarMenu(
    isRunning: Boolean,
    onRunAnalysis: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TitleBarMenuButton(
            text = DesktopTitleBarText.RUN,
            enabled = !isRunning,
            onClick = { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TitleBarDropdownItem(
                text = if (isRunning) DesktopTitleBarText.RUNNING else DesktopTitleBarText.RUN_ANALYSIS,
                enabled = !isRunning,
                onClick = {
                    expanded = false
                    onRunAnalysis()
                }
            )
        }
    }
}

@Composable
private fun TitleBarDropdownItem(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    val textColor = if (enabled) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.48f)
    }
    val highlightColor = when {
        !enabled -> Color.Transparent
        pressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        hovered -> MaterialTheme.colorScheme.primary.copy(alpha = 0.11f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .widthIn(min = 176.dp)
            .padding(horizontal = 6.dp)
            .height(28.dp)
            .background(
                color = highlightColor,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun TitleBarIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (Color) -> Unit
) {
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier
            .size(width = 42.dp, height = 42.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon(iconColor)
    }
}

@Composable
private fun WindowControlButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (Color) -> Unit
) {
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier
            .size(width = 46.dp, height = 42.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon(iconColor)
    }
}

@Composable
private fun ThemeIcon(
    color: Color,
    showDarkAction: Boolean,
    modifier: Modifier = Modifier
) {
    val cutoutColor = MaterialTheme.colorScheme.surface
    Canvas(modifier = modifier.size(18.dp)) {
        if (showDarkAction) {
            drawCircle(
                color = color,
                radius = 7.dp.toPx(),
                center = center
            )
            drawCircle(
                color = cutoutColor,
                radius = 6.dp.toPx(),
                center = Offset(center.x + 4.dp.toPx(), center.y - 3.dp.toPx())
            )
        } else {
            val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
            drawCircle(
                color = color,
                radius = 5.dp.toPx(),
                center = center,
                style = Stroke(width = 1.6.dp.toPx())
            )
            repeat(8) { index ->
                val angle = Math.toRadians((index * 45).toDouble())
                val startRadius = 7.dp.toPx()
                val endRadius = 8.5.dp.toPx()
                val start = Offset(
                    x = center.x + kotlin.math.cos(angle).toFloat() * startRadius,
                    y = center.y + kotlin.math.sin(angle).toFloat() * startRadius
                )
                val end = Offset(
                    x = center.x + kotlin.math.cos(angle).toFloat() * endRadius,
                    y = center.y + kotlin.math.sin(angle).toFloat() * endRadius
                )
                drawLine(
                    color = color,
                    start = start,
                    end = end,
                    strokeWidth = stroke.width,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun RestoreIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(18.dp)) {
        val stroke = Stroke(width = 1.1.dp.toPx())
        drawRect(
            color = color,
            topLeft = Offset(6.5.dp.toPx(), 4.5.dp.toPx()),
            size = Size(7.dp.toPx(), 7.dp.toPx()),
            style = stroke
        )
        drawRect(
            color = color,
            topLeft = Offset(4.5.dp.toPx(), 6.5.dp.toPx()),
            size = Size(7.dp.toPx(), 7.dp.toPx()),
            style = stroke
        )
    }
}

@Composable
private fun MinimizeIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(18.dp)) {
        drawLine(
            color = color,
            start = Offset(4.dp.toPx(), 12.dp.toPx()),
            end = Offset(14.dp.toPx(), 12.dp.toPx()),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun MaximizeIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(18.dp)) {
        drawRect(
            color = color,
            topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
            size = Size(10.dp.toPx(), 10.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

@Composable
private fun CloseIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.6.dp.toPx()
        drawLine(
            color = color,
            start = Offset(5.dp.toPx(), 5.dp.toPx()),
            end = Offset(13.dp.toPx(), 13.dp.toPx()),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(13.dp.toPx(), 5.dp.toPx()),
            end = Offset(5.dp.toPx(), 13.dp.toPx()),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
