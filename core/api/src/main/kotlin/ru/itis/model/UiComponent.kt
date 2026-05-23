package ru.itis.model

import kotlinx.serialization.Serializable

@Serializable
data class UiComponent(
    val id: String?,
    val type: String,
    val sourceType: SourceType,
    val filePath: String,
    val treePath: String? = null,
    val properties: UiProperties,
    val children: List<UiComponent> = emptyList()
)
