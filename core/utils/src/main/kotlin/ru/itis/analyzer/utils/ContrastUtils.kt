package ru.itis.analyzer.utils

import ru.itis.analyzer.config.analyzer.AnalyzerThresholds
import kotlin.math.pow

object ContrastUtils {

    fun calculateContrastRatio(color1: String?, color2: String?): Double? {
        val rgb1 = ColorUtils.toRgb(color1) ?: return null
        val rgb2 = ColorUtils.toRgb(color2) ?: return null

        val l1 = luminance(rgb1)
        val l2 = luminance(rgb2)

        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)

        return (lighter + AnalyzerThresholds.CONTRAST_OFFSET) /
                (darker + AnalyzerThresholds.CONTRAST_OFFSET)
    }

    private fun luminance(rgb: Triple<Int, Int, Int>): Double {
        val (r, g, b) = rgb

        val rs = normalize(r)
        val gs = normalize(g)
        val bs = normalize(b)

        return AnalyzerThresholds.RED_LUMINANCE_WEIGHT * rs +
                AnalyzerThresholds.GREEN_LUMINANCE_WEIGHT * gs +
                AnalyzerThresholds.BLUE_LUMINANCE_WEIGHT * bs
    }

    private fun normalize(value: Int): Double {
        val v = value / AnalyzerThresholds.RGB_MAX_CHANNEL_VALUE
        return if (v <= AnalyzerThresholds.SRGB_LINEAR_THRESHOLD) {
            v / AnalyzerThresholds.SRGB_LOW_RANGE_DIVISOR
        } else {
            ((v + AnalyzerThresholds.SRGB_GAMMA_OFFSET) / AnalyzerThresholds.SRGB_GAMMA_DIVISOR)
                .pow(AnalyzerThresholds.SRGB_GAMMA_POWER)
        }
    }
}
