package ru.itis.analyzer.utils

import ru.itis.analyzer.config.analyzer.AnalyzerThresholds
import ru.itis.analyzer.config.components.ResourcePatterns
import kotlin.math.sqrt

object ColorUtils {

    fun normalizeHexColor(value: String?): String? {
        if (value == null) return null

        val normalized = value.trim().uppercase()

        return when {
            normalized.matches(Regex(ResourcePatterns.HEX_RGB_PATTERN)) -> normalized
            normalized.matches(Regex(ResourcePatterns.HEX_ARGB_PATTERN)) -> normalized
            else -> null
        }
    }

    fun isHexColor(value: String?): Boolean {
        return normalizeHexColor(value) != null
    }

    fun extractComparableColor(value: String?): String? {
        return normalizeHexColor(value)
    }

    fun toRgb(color: String?): Triple<Int, Int, Int>? {
        val normalized = normalizeHexColor(color) ?: return null

        return when (normalized.length) {
            7 -> {
                val r = normalized.substring(1, 3).toInt(16)
                val g = normalized.substring(3, 5).toInt(16)
                val b = normalized.substring(5, 7).toInt(16)
                Triple(r, g, b)
            }

            9 -> {
                val r = normalized.substring(3, 5).toInt(16)
                val g = normalized.substring(5, 7).toInt(16)
                val b = normalized.substring(7, 9).toInt(16)
                Triple(r, g, b)
            }

            else -> null
        }
    }

    fun colorDistance(first: String?, second: String?): Double? {
        val firstRgb = toRgb(first) ?: return null
        val secondRgb = toRgb(second) ?: return null

        val dr = (firstRgb.first - secondRgb.first).toDouble()
        val dg = (firstRgb.second - secondRgb.second).toDouble()
        val db = (firstRgb.third - secondRgb.third).toDouble()

        return sqrt(dr * dr + dg * dg + db * db)
    }

    fun areColorsClose(first: String?, second: String?, threshold: Double = 12.0): Boolean {
        val distance = colorDistance(first, second) ?: return false
        return distance in AnalyzerThresholds.MINIMUM_NON_ZERO_COLOR_DISTANCE..threshold
    }
}
