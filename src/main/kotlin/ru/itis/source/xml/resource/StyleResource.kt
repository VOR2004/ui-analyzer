package ru.itis.source.xml.resource

data class StyleResource(
    val name: String,
    val parent: String?,
    val items: Map<String, String>
)
