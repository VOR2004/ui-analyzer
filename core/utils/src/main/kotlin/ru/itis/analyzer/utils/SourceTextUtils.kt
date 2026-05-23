package ru.itis.analyzer.utils

object SourceTextUtils {

    fun findMatchingDelimiter(
        source: String,
        openIndex: Int,
        openDelimiter: Char,
        closeDelimiter: Char
    ): Int? {
        var depth = 0
        var inString = false

        for (index in openIndex until source.length) {
            val char = source[index]
            when {
                char == '"' -> inString = !inString
                !inString && char == openDelimiter -> depth++
                !inString && char == closeDelimiter -> {
                    depth--
                    if (depth == 0) return index
                }
            }
        }

        return null
    }
}
