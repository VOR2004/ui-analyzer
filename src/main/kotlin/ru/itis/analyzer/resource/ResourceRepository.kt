package ru.itis.analyzer.resource

import ru.itis.analyzer.config.ProjectStructure
import ru.itis.analyzer.config.ResourcePatterns
import ru.itis.analyzer.utils.ColorUtils
import java.io.File

class ResourceRepository(
    private val colors: Map<String, String>
) {

    fun resolveColor(value: String?): String? {
        if (value == null) return null

        val trimmed = value.trim()

        ColorUtils.normalizeHexColor(trimmed)?.let { return it }

        if (trimmed.startsWith(ResourcePatterns.COLOR_REF_PREFIX)) {
            val colorName = trimmed.removePrefix(ResourcePatterns.COLOR_REF_PREFIX)
            val rawValue = colors[colorName] ?: return null

            return resolveColor(rawValue)
        }

        if (trimmed.startsWith(ResourcePatterns.ANDROID_COLOR_REF_PREFIX)) {
            return null
        }

        if (trimmed.startsWith(ResourcePatterns.ATTR_REF_PREFIX)) {
            return null
        }

        return null
    }

    companion object {
        fun load(projectRoot: File): ResourceRepository {
            val parser = ValuesColorParser()
            val allColors = mutableMapOf<String, String>()

            val valuesFiles = projectRoot
                .walkTopDown()
                .filter { file ->
                    file.isFile &&
                    file.extension == ProjectStructure.XML_EXTENSION &&
                    file.parentFile?.name?.startsWith(ProjectStructure.VALUES_DIRECTORY_PREFIX) == true
                }
                .toList()

            for (file in valuesFiles) {
                runCatching {
                    parser.parseColors(file)
                }.onSuccess { parsed ->
                    allColors.putAll(parsed)
                }
            }

            return ResourceRepository(allColors)
        }
    }
}
