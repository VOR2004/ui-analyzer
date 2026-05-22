package ru.itis.model

data class TreeNodeContext(
    val component: UiComponent,
    val parent: UiComponent?,
    val depth: Int,
    val path: List<UiComponent>
)