package ru.itis.compose.rules.runtime

import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.compose.runtime.model.ComposeRuntimeAttributes
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiBounds
import ru.itis.model.UiComponent

class RuntimeScreenDensitySnapshotInfoRule : Rule {
    override val id: String = RuleIds.RUNTIME_SCREEN_DENSITY_SNAPSHOT_INFO

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return components
            .filter { root -> root.sourceType in runtimeSourceTypes }
            .groupBy { root -> root.snapshotKey() }
            .values
            .mapNotNull { roots -> buildIssue(roots.first()) }
    }

    private fun buildIssue(root: UiComponent): AnalysisIssue? {
        val bounds = root.screenBounds()
        val size = root.metadataScreenSize() ?: bounds?.toSizeString() ?: UNKNOWN_VALUE
        val orientation = root.runtimeAttribute(ComposeRuntimeAttributes.ORIENTATION)
            ?: bounds?.orientation()
            ?: UNKNOWN_VALUE
        val density = root.runtimeDensity()

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.INFO,
            componentId = root.id,
            componentLocator = root.treePath?.let { path -> "${root.type}[path=$path]" },
            componentType = SNAPSHOT_COMPONENT_TYPE,
            filePath = root.filePath,
            message = AnalyzerMessages.runtimeScreenDensitySnapshotInfo(
                source = root.sourceType.name,
                screen = root.runtimeAttribute(ComposeRuntimeAttributes.SCREEN) ?: UNKNOWN_VALUE,
                state = root.runtimeAttribute(ComposeRuntimeAttributes.STATE) ?: UNKNOWN_VALUE,
                size = size,
                orientation = orientation,
                density = density
            ),
            recommendation = AnalyzerMessages.RUNTIME_SCREEN_DENSITY_SNAPSHOT_INFO_RECOMMENDATION
        )
    }

    private fun UiComponent.snapshotKey(): String {
        return listOf(
            filePath,
            sourceType.name,
            runtimeAttribute(ComposeRuntimeAttributes.SCREEN).orEmpty(),
            runtimeAttribute(ComposeRuntimeAttributes.STATE).orEmpty()
        ).joinToString(separator = "|")
    }

    private fun UiComponent.screenBounds(): UiBounds? {
        return properties.bounds ?: ComponentUtils.flatten(this)
            .mapNotNull { component -> component.properties.bounds }
            .takeIf { bounds -> bounds.isNotEmpty() }
            ?.let { bounds -> bounds.union() }
    }

    private fun List<UiBounds>.union(): UiBounds {
        val left = minOf { bounds -> bounds.x }
        val top = minOf { bounds -> bounds.y }
        val right = maxOf { bounds -> bounds.x + bounds.width }
        val bottom = maxOf { bounds -> bounds.y + bounds.height }
        return UiBounds(
            x = left,
            y = top,
            width = right - left,
            height = bottom - top
        )
    }

    private fun UiComponent.metadataScreenSize(): String? {
        val width = runtimeAttribute(ComposeRuntimeAttributes.SCREEN_WIDTH_PX) ?: return null
        val height = runtimeAttribute(ComposeRuntimeAttributes.SCREEN_HEIGHT_PX) ?: return null
        return "${width}px x ${height}px"
    }

    private fun UiComponent.runtimeDensity(): String {
        val density = runtimeAttribute(ComposeRuntimeAttributes.DENSITY)
        val densityDpi = runtimeAttribute(ComposeRuntimeAttributes.DENSITY_DPI)
        return listOfNotNull(
            density?.let { value -> "density=$value" },
            densityDpi?.let { value -> "densityDpi=$value" }
        ).joinToString(", ").ifBlank { UNKNOWN_VALUE }
    }

    private fun UiComponent.runtimeAttribute(name: String): String? {
        return properties.rawAttributes[name]?.takeIf { value -> value.isNotBlank() }
    }

    private fun UiBounds.toSizeString(): String {
        return "${width.toPixelString()} x ${height.toPixelString()}"
    }

    private fun UiBounds.orientation(): String {
        return when {
            width > height -> LANDSCAPE_ORIENTATION
            height > width -> PORTRAIT_ORIENTATION
            else -> SQUARE_ORIENTATION
        }
    }

    private fun Float.toPixelString(): String {
        return if (this % 1f == 0f) {
            "${toInt()}px"
        } else {
            "${this}px"
        }
    }

    private companion object {
        const val SNAPSHOT_COMPONENT_TYPE = "RuntimeSnapshot"
        const val UNKNOWN_VALUE = "unknown"
        const val LANDSCAPE_ORIENTATION = "landscape"
        const val PORTRAIT_ORIENTATION = "portrait"
        const val SQUARE_ORIENTATION = "square"
        val runtimeSourceTypes = setOf(SourceType.COMPOSE_RUNTIME, SourceType.ANDROID_RUNTIME)
    }
}
