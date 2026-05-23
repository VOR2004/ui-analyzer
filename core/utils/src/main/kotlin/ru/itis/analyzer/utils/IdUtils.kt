package ru.itis.analyzer.utils

import ru.itis.analyzer.config.components.ResourcePatterns

object IdUtils {

    fun normalizeId(id: String?): String? {
        if (id == null) return null

        return id
            .removePrefix(ResourcePatterns.ANDROID_NEW_ID_PREFIX)
            .removePrefix(ResourcePatterns.ANDROID_ID_PREFIX)
            .trim()
            .ifBlank { null }
    }
}
