package ru.itis.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectModel(
    val components: List<UiComponent>
)