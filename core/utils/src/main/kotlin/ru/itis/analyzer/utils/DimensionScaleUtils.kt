package ru.itis.analyzer.utils

object DimensionScaleUtils {

    fun guessBaseUnit(values: List<Float>): Float? {
        if (values.isEmpty()) {
            return null
        }

        return BASE_UNIT_CANDIDATES.firstOrNull { unit ->
            values.count { value ->
                val remainder = value % unit
                remainder <= BASE_UNIT_EPSILON || remainder >= unit - BASE_UNIT_EPSILON
            } >= values.size / MIN_MATCH_DIVISOR
        }
    }

    private const val BASE_UNIT_EPSILON = 0.1f
    private const val MIN_MATCH_DIVISOR = 2
    private val BASE_UNIT_CANDIDATES = listOf(4f, 8f, 2f)
}
