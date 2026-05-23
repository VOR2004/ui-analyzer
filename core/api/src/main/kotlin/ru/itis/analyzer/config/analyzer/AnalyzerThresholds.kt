package ru.itis.analyzer.config.analyzer

object AnalyzerThresholds {
    const val NEAR_COLOR_DISTANCE = 12.0
    const val MIN_SUSPICIOUS_TEXT_SIZE_SP = 8f
    const val LARGE_TEXT_THRESHOLD_SP = 18f
    const val MAX_SUSPICIOUS_TEXT_SIZE_SP = 40f
    const val LARGE_TEXT_MIN_CONTRAST = 3.0
    const val NORMAL_TEXT_MIN_CONTRAST = 4.5
    const val MINIMUM_NON_ZERO_COLOR_DISTANCE = 0.000001
    const val CONTRAST_OFFSET = 0.05
    const val SRGB_LINEAR_THRESHOLD = 0.03928
    const val SRGB_LOW_RANGE_DIVISOR = 12.92
    const val SRGB_GAMMA_OFFSET = 0.055
    const val SRGB_GAMMA_DIVISOR = 1.055
    const val SRGB_GAMMA_POWER = 2.4
    const val RGB_MAX_CHANNEL_VALUE = 255.0
    const val RED_LUMINANCE_WEIGHT = 0.2126
    const val GREEN_LUMINANCE_WEIGHT = 0.7152
    const val BLUE_LUMINANCE_WEIGHT = 0.0722
}