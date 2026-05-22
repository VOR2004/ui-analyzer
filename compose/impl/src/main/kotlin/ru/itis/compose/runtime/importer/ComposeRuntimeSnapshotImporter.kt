package ru.itis.compose.runtime.importer

import java.io.File
import kotlinx.serialization.json.Json
import ru.itis.compose.runtime.model.ComposeRuntimeComponent
import ru.itis.compose.runtime.model.ComposeRuntimeSnapshot
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeRuntimeSnapshotImporter {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun import(snapshotFile: File): List<UiComponent> {
        val snapshot = json.decodeFromString<ComposeRuntimeSnapshot>(snapshotFile.readText())
        return snapshot.components.mapIndexed { index, component ->
            component.toUiComponent(
                snapshot = snapshot,
                snapshotFile = snapshotFile,
                parentPath = "",
                siblingIndex = index
            )
        }
    }

    private fun ComposeRuntimeComponent.toUiComponent(
        snapshot: ComposeRuntimeSnapshot,
        snapshotFile: File,
        parentPath: String,
        siblingIndex: Int
    ): UiComponent {
        val resolvedTreePath = treePath ?: "$parentPath/$type[$siblingIndex]"
        return UiComponent(
            id = id,
            type = type,
            sourceType = SourceType.COMPOSE_RUNTIME,
            filePath = filePath ?: snapshotFile.absolutePath,
            treePath = resolvedTreePath,
            properties = toUiProperties(snapshot),
            children = children.mapIndexed { index, child ->
                child.toUiComponent(
                    snapshot = snapshot,
                    snapshotFile = snapshotFile,
                    parentPath = resolvedTreePath,
                    siblingIndex = index
                )
            }
        )
    }

    private fun ComposeRuntimeComponent.toUiProperties(snapshot: ComposeRuntimeSnapshot): UiProperties {
        return UiProperties(
            width = bounds?.width?.toPixelString(),
            height = bounds?.height?.toPixelString(),
            backgroundColor = backgroundColor,
            textColor = textColor,
            textSize = textSize,
            text = text,
            contentDescription = contentDescription,
            isClickable = isClickable,
            bounds = bounds,
            rawAttributes = buildMap {
                putAll(attributes)
                put(SCREEN_ATTRIBUTE, snapshot.screen)
                snapshot.state?.let { state -> put(STATE_ATTRIBUTE, state) }
                locator?.let { locator -> put(LOCATOR_ATTRIBUTE, locator) }
                bounds?.let { bounds ->
                    put(BOUNDS_X_ATTRIBUTE, bounds.x.toPixelString())
                    put(BOUNDS_Y_ATTRIBUTE, bounds.y.toPixelString())
                }
            }
        )
    }

    private fun Float.toPixelString(): String {
        return if (this % 1f == 0f) {
            "${toInt()}px"
        } else {
            "${this}px"
        }
    }

    private companion object {
        const val SCREEN_ATTRIBUTE = "runtime:screen"
        const val STATE_ATTRIBUTE = "runtime:state"
        const val LOCATOR_ATTRIBUTE = "runtime:locator"
        const val BOUNDS_X_ATTRIBUTE = "runtime:boundsX"
        const val BOUNDS_Y_ATTRIBUTE = "runtime:boundsY"
    }
}
