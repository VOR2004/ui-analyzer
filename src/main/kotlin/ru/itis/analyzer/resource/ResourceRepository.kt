package ru.itis.analyzer.resource

import ru.itis.analyzer.config.ProjectStructure
import ru.itis.analyzer.config.ResourcePatterns
import ru.itis.analyzer.utils.ColorUtils
import java.io.File

class ResourceRepository(
    private val colors: Map<String, String>,
    private val dimensions: Map<String, String> = emptyMap(),
    private val strings: Map<String, String> = emptyMap(),
    private val styles: Map<String, StyleResource> = emptyMap()
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

        if (isAttrReference(trimmed)) {
            return resolveColor(resolveThemeAttribute(trimmed))
        }

        return null
    }

    fun resolveDimension(value: String?): String? {
        if (value == null) return null

        val trimmed = value.trim()
        if (trimmed.endsWith(ResourcePatterns.DP_UNIT) || trimmed.endsWith(ResourcePatterns.SP_UNIT)) {
            return trimmed
        }

        if (trimmed.startsWith(ResourcePatterns.DIMEN_REF_PREFIX)) {
            val dimensionName = trimmed.removePrefix(ResourcePatterns.DIMEN_REF_PREFIX)
            val rawValue = dimensions[dimensionName] ?: return null

            return resolveDimension(rawValue)
        }

        if (isAttrReference(trimmed)) {
            return resolveDimension(resolveThemeAttribute(trimmed))
        }

        return null
    }

    fun resolveString(value: String?): String? {
        if (value == null) return null

        val trimmed = value.trim()
        if (trimmed.startsWith(ResourcePatterns.STRING_REF_PREFIX)) {
            val stringName = trimmed.removePrefix(ResourcePatterns.STRING_REF_PREFIX)
            return strings[stringName]
        }

        if (trimmed.startsWith(ResourcePatterns.ANDROID_STRING_REF_PREFIX)) {
            return null
        }

        return trimmed
    }

    fun resolveStyleItem(styleName: String, itemName: String): String? {
        return resolveStyleItem(
            styleName = normalizeStyleName(styleName),
            itemName = itemName,
            visited = emptySet()
        )
    }

    fun resolveThemeAttribute(attributeReference: String?): String? {
        if (attributeReference == null) return null

        val attributeNames = normalizeAttributeReference(attributeReference)
        if (attributeNames.isEmpty()) {
            return null
        }

        val candidates = styles.values
            .flatMap { style ->
                attributeNames.mapNotNull { attributeName ->
                    resolveStyleItem(style.name, attributeName)
                }
            }
            .distinct()

        return candidates.singleOrNull()
    }

    private fun resolveStyleItem(
        styleName: String,
        itemName: String,
        visited: Set<String>
    ): String? {
        if (styleName in visited) {
            return null
        }

        val style = styles[styleName] ?: return null
        style.items[itemName]?.let { return it }

        val parentName = style.parent?.let { normalizeStyleName(it) } ?: return null
        return resolveStyleItem(
            styleName = parentName,
            itemName = itemName,
            visited = visited + styleName
        )
    }

    private fun normalizeStyleName(value: String): String {
        return value.trim().removePrefix(ResourcePatterns.STYLE_REF_PREFIX)
    }

    private fun normalizeAttributeReference(value: String): List<String> {
        val trimmed = value.trim()

        return when {
            trimmed.startsWith(ResourcePatterns.ATTR_REF_PREFIX) -> {
                val name = trimmed.removePrefix(ResourcePatterns.ATTR_REF_PREFIX)
                listOf(name)
            }
            trimmed.startsWith(ResourcePatterns.ANDROID_ATTR_REF_PREFIX) -> {
                val name = trimmed.removePrefix(ResourcePatterns.ANDROID_ATTR_REF_PREFIX)
                listOf("android:$name", name)
            }
            trimmed.isNotBlank() -> listOf(trimmed)
            else -> emptyList()
        }
    }

    private fun isAttrReference(value: String): Boolean {
        return value.startsWith(ResourcePatterns.ATTR_REF_PREFIX) ||
            value.startsWith(ResourcePatterns.ANDROID_ATTR_REF_PREFIX)
    }

    companion object {
        fun empty(): ResourceRepository {
            return ResourceRepository(
                colors = emptyMap(),
                dimensions = emptyMap(),
                strings = emptyMap(),
                styles = emptyMap()
            )
        }

        fun load(projectRoot: File): ResourceRepository {
            val parser = ValuesResourceParser()
            val allColors = mutableMapOf<String, String>()
            val allDimensions = mutableMapOf<String, String>()
            val allStrings = mutableMapOf<String, String>()
            val allStyles = mutableMapOf<String, StyleResource>()

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
                    parser.parse(file)
                }.onSuccess { resources ->
                    allColors.putAll(resources.colors)
                    allDimensions.putAll(resources.dimensions)
                    allStrings.putAll(resources.strings)
                    allStyles.putAll(resources.styles)
                }
            }

            return ResourceRepository(
                colors = allColors,
                dimensions = allDimensions,
                strings = allStrings,
                styles = allStyles
            )
        }
    }
}
