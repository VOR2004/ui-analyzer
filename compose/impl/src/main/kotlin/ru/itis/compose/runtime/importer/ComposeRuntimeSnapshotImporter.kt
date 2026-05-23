package ru.itis.compose.runtime.importer

import java.io.File
import kotlinx.serialization.json.Json
import ru.itis.compose.runtime.model.ComposeRuntimeAttributes
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
                put(ComposeRuntimeAttributes.SCREEN, snapshot.screen)
                snapshot.state?.let { state -> put(ComposeRuntimeAttributes.STATE, state) }
                locator?.let { locator -> put(ComposeRuntimeAttributes.LOCATOR, locator) }
                bounds?.let { bounds ->
                    put(ComposeRuntimeAttributes.BOUNDS_X, bounds.x.toPixelString())
                    put(ComposeRuntimeAttributes.BOUNDS_Y, bounds.y.toPixelString())
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
}
