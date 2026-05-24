package ru.itis.compose.runtime.utils

import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.RuntimeAttributes
import ru.itis.model.UiBounds
import ru.itis.model.UiComponent

object RuntimeScreenMetrics {

    fun estimateDensity(root: UiComponent): Float {
        root.properties.rawAttributes[RuntimeAttributes.DENSITY]
            ?.toFloatOrNull()
            ?.takeIf { density -> density > 0f }
            ?.let { density -> return density }

        root.properties.rawAttributes[RuntimeAttributes.DENSITY_DPI]
            ?.toFloatOrNull()
            ?.takeIf { densityDpi -> densityDpi > 0f }
            ?.let { densityDpi -> return densityDpi / BASE_DENSITY_DPI }

        val bounds = root.properties.bounds ?: inferBounds(root) ?: return DEFAULT_DENSITY
        val shortestSide = minOf(bounds.width, bounds.height)
        return (shortestSide / BASE_PHONE_WIDTH_DP).coerceIn(MIN_DENSITY, MAX_DENSITY)
    }

    fun inferBounds(root: UiComponent): UiBounds? {
        return ComponentUtils.flatten(root)
                .mapNotNull { component -> component.properties.bounds }
                .takeIf { bounds -> bounds.isNotEmpty() }?.union()
    }

    private fun List<UiBounds>.union(): UiBounds {
        val left = minOf { bounds -> bounds.x }
        val top = minOf { bounds -> bounds.y }
        val right = maxOf { bounds -> bounds.x + bounds.width }
        val bottom = maxOf { bounds -> bounds.y + bounds.height }
        return UiBounds(
            x = left,
            y = top,
            width = right - left,
            height = bottom - top
        )
    }

    private const val BASE_DENSITY_DPI = 160f
    private const val BASE_PHONE_WIDTH_DP = 411f
    private const val DEFAULT_DENSITY = 1f
    private const val MIN_DENSITY = 1f
    private const val MAX_DENSITY = 4f
}
