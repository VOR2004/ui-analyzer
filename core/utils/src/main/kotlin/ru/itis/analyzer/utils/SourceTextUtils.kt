package ru.itis.analyzer.utils

object SourceTextUtils {

    fun findMatchingDelimiter(
        source: String,
        openIndex: Int,
        openDelimiter: Char,
        closeDelimiter: Char,
        endIndex: Int = source.length
    ): Int? {
        var depth = 0
        var inString = false
        var escaped = false

        for (index in openIndex until endIndex.coerceAtMost(source.length)) {
            val char = source[index]
            if (inString) {
                if (escaped) {
                    escaped = false
                    continue
                }

                when (char) {
                    '\\' -> escaped = true
                    '"' -> inString = false
                }
                continue
            }

            when (char) {
                '"' -> inString = true
                openDelimiter -> depth++
                closeDelimiter -> {
                    depth--
                    if (depth == 0) return index
                }
            }
        }

        return null
    }
}
