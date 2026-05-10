package ru.itis.compose.style

import ru.itis.analyzer.utils.ColorUtils

object ComposeColorValueNormalizer {

    fun normalize(value: String?, includeThemeTokens: Boolean = true): String? {
        val trimmed = value?.trim() ?: return null
        ColorUtils.normalizeHexColor(trimmed)?.let { color -> return color }

        val constructorMatch = COLOR_CONSTRUCTOR_PATTERN.matchEntire(trimmed)
        if (constructorMatch != null) {
            return ColorUtils.normalizeHexColor("#${constructorMatch.groupValues[1]}")
        }

        val argbMatch = ARGB_LITERAL_PATTERN.matchEntire(trimmed)
        if (argbMatch != null) {
            return ColorUtils.normalizeHexColor("#${argbMatch.groupValues[1]}")
        }

        return trimmed
            .takeIf { includeThemeTokens }
            ?.takeIf { color -> color.startsWith(MATERIAL_THEME_COLOR_PREFIX) }
    }

    private const val MATERIAL_THEME_COLOR_PREFIX = "MaterialTheme.colorScheme."
    private val COLOR_CONSTRUCTOR_PATTERN = Regex("""Color\(0x([0-9A-Fa-f]{8})\)""")
    private val ARGB_LITERAL_PATTERN = Regex("""0x([0-9A-Fa-f]{8})""")
}
