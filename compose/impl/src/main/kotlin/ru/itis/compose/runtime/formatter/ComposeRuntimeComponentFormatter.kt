package ru.itis.compose.runtime.formatter

import ru.itis.model.UiComponent

object ComposeRuntimeComponentFormatter {

    fun describe(component: UiComponent): String {
        return listOfNotNull(
            component.type,
            component.id?.let { id -> "id=$id" },
            component.treePath?.let { path -> "path=$path" },
            component.properties.text?.let { text -> "text=$text" }
        ).joinToString(prefix = "[", postfix = "]")
    }
}
