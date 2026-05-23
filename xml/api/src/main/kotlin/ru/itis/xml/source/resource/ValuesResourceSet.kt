package ru.itis.xml.source.resource

data class ValuesResourceSet(
    val colors: Map<String, String> = emptyMap(),
    val dimensions: Map<String, String> = emptyMap(),
    val strings: Map<String, String> = emptyMap(),
    val styles: Map<String, StyleResource> = emptyMap()
)
