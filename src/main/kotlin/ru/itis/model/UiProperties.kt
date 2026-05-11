package ru.itis.model

import kotlinx.serialization.Serializable

@Serializable
data class UiProperties(
    val width: String? = null,
    val height: String? = null,
    val padding: String? = null,
    val margin: String? = null,
    val backgroundColor: String? = null,
    val backgroundTint: String? = null,
    val tint: String? = null,
    val textColor: String? = null,
    val textSize: String? = null,
    val typographyStyle: String? = null,
    val fontFamily: String? = null,
    val textStyle: String? = null,
    val text: String? = null,
    val contentDescription: String? = null,
    val isClickable: Boolean = false,
    val bounds: UiBounds? = null,
    val rawAttributes: Map<String, String> = emptyMap()
)
