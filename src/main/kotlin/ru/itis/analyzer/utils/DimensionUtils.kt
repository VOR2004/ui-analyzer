package ru.itis.analyzer.utils

import ru.itis.analyzer.config.ResourcePatterns

object DimensionUtils {

    fun parseSp(value: String?): Float? {
        return parseUnit(value, ResourcePatterns.SP_UNIT)
    }

    fun parseDp(value: String?): Float? {
        return parseUnit(value, ResourcePatterns.DP_UNIT)
    }

    fun isSp(value: String?): Boolean {
        return value?.trim()?.lowercase()?.endsWith(ResourcePatterns.SP_UNIT) == true
    }

    fun isDp(value: String?): Boolean {
        return value?.trim()?.lowercase()?.endsWith(ResourcePatterns.DP_UNIT) == true
    }

    private fun parseUnit(value: String?, unit: String): Float? {
        if (value == null) return null

        val normalized = value.trim().lowercase()
        if (!normalized.endsWith(unit)) return null

        return normalized
            .removeSuffix(unit)
            .trim()
            .toFloatOrNull()
    }
}
