package ru.itis.model

import kotlinx.serialization.Serializable

@Serializable
data class UiBounds(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)
