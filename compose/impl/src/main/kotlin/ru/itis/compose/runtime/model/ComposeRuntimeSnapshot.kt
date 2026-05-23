package ru.itis.compose.runtime.model

import kotlinx.serialization.Serializable
import ru.itis.model.UiBounds

@Serializable
data class ComposeRuntimeSnapshot(
    val screen: String,
    val state: String? = null,
    val density: Float? = null,
    val densityDpi: Int? = null,
    val orientation: String? = null,
    val screenWidthPx: Int? = null,
    val screenHeightPx: Int? = null,
    val components: List<ComposeRuntimeComponent> = emptyList()
)

@Serializable
data class ComposeRuntimeComponent(
    val id: String? = null,
    val type: String,
    val locator: String? = null,
    val filePath: String? = null,
    val treePath: String? = null,
    val text: String? = null,
    val contentDescription: String? = null,
    val backgroundColor: String? = null,
    val textColor: String? = null,
    val textSize: String? = null,
    val isClickable: Boolean = false,
    val bounds: UiBounds? = null,
    val attributes: Map<String, String> = emptyMap(),
    val children: List<ComposeRuntimeComponent> = emptyList()
)
