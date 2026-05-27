package ru.itis.compose.runtime.formatter

import ru.itis.model.UiComponent

object ComposeRuntimeComponentFormatter {

    fun describe(component: UiComponent): String {
        return listOfNotNull(
            component.type,
            component.id?.let { id -> "id=$id" },
            component.properties.bounds?.let { bounds ->
                "bounds=[x=${bounds.x.toPixelString()}, y=${bounds.y.toPixelString()}, " +
                    "w=${bounds.width.toPixelString()}, h=${bounds.height.toPixelString()}]"
            },
            component.properties.contentDescription
                ?.takeIf { value -> value.isMeaningfulRuntimeText() }
                ?.let { text -> "contentDescription=$text" },
            component.properties.text
                ?.takeIf { value -> value.isMeaningfulRuntimeText() }
                ?.let { text -> "text=$text" }
        ).joinToString(prefix = "[", postfix = "]")
    }

    private fun Float.toPixelString(): String {
        return if (this % 1f == 0f) {
            "${toInt()}px"
        } else {
            "${this}px"
        }
    }

    private fun String.isMeaningfulRuntimeText(): Boolean {
        val value = trim()
        return value.isNotBlank() && !value.equals(NULL_TEXT, ignoreCase = true)
    }

    private const val NULL_TEXT = "null"
}
