package ru.itis.compose.style.signature

data class ComposeTextStyleSignature(
    val role: ComposePredictedTextRole,
    val typographyStyle: String?,
    val textSize: Float?,
    val textStyle: String?,
    val fontFamily: String?
)
