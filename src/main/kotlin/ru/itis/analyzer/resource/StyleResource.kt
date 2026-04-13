package ru.itis.analyzer.resource

data class StyleResource(
    val name: String,
    val parent: String?,
    val items: Map<String, String>
)
