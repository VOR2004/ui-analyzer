package ru.itis.desktop.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun RulesIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(18.dp)) {
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        val left = 3.dp.toPx()
        val right = size.width - 3.dp.toPx()
        val top = 4.dp.toPx()
        val gap = 5.dp.toPx()

        repeat(3) { index ->
            val y = top + gap * index
            drawCircle(color = color, radius = 1.3.dp.toPx(), center = Offset(left, y))
            drawLine(color = color, start = Offset(left + 4.dp.toPx(), y), end = Offset(right, y), strokeWidth = stroke.width, cap = StrokeCap.Round)
        }
    }
}

@Composable
fun SelectAllIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(18.dp)) {
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        drawRoundRect(
            color = color,
            topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
            size = Size(12.dp.toPx(), 12.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
            style = stroke
        )
        drawLine(
            color = color,
            start = Offset(6.dp.toPx(), 9.dp.toPx()),
            end = Offset(8.dp.toPx(), 11.dp.toPx()),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(8.dp.toPx(), 11.dp.toPx()),
            end = Offset(13.dp.toPx(), 6.dp.toPx()),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ClearSelectionIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.8.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
            size = Size(12.dp.toPx(), 12.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = color,
            start = Offset(6.dp.toPx(), 6.dp.toPx()),
            end = Offset(12.dp.toPx(), 12.dp.toPx()),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(12.dp.toPx(), 6.dp.toPx()),
            end = Offset(6.dp.toPx(), 12.dp.toPx()),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun CollapseIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(18.dp)) {
        drawLine(
            color = color,
            start = Offset(5.dp.toPx(), size.height / 2f),
            end = Offset(13.dp.toPx(), size.height / 2f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
