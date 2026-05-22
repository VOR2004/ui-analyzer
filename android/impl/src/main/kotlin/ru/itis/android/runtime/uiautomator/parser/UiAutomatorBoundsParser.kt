package ru.itis.android.runtime.uiautomator.parser

import ru.itis.model.UiBounds

internal class UiAutomatorBoundsParser {

    fun parse(rawBounds: String?): UiBounds? {
        val value = rawBounds?.trim().orEmpty()
        val match = BOUNDS_PATTERN.matchEntire(value) ?: return null

        val left = match.groupValues[1].toFloat()
        val top = match.groupValues[2].toFloat()
        val right = match.groupValues[3].toFloat()
        val bottom = match.groupValues[4].toFloat()
        val width = right - left
        val height = bottom - top

        if (width < 0f || height < 0f) return null

        return UiBounds(
            x = left,
            y = top,
            width = width,
            height = height
        )
    }

    private val BOUNDS_PATTERN = Regex("""\[(\d+),(\d+)]\[(\d+),(\d+)]""")
}
