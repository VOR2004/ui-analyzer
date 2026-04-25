package ru.itis.source.compose.importer

import java.io.File
import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.config.ProjectStructure

class ComposeProjectImporter {

    fun findComposeKotlinFiles(projectRoot: File): List<File> {
        if (!projectRoot.exists() || !projectRoot.isDirectory) {
            return emptyList()
        }

        return projectRoot
            .walkTopDown()
            .onEnter { directory -> !shouldSkipDirectory(directory) }
            .filter { file ->
                file.isFile &&
                    file.extension == ProjectStructure.KOTLIN_EXTENSION &&
                    looksLikeComposeFile(file)
            }
            .sortedBy { it.absolutePath }
            .toList()
    }

    private fun looksLikeComposeFile(file: File): Boolean {
        val content = runCatching { file.readText() }.getOrNull() ?: return false
        return content.contains(COMPOSABLE_ANNOTATION) ||
            composeFunctionNames.any { functionName -> content.contains("$functionName(") }
    }

    private fun shouldSkipDirectory(directory: File): Boolean {
        return directory.name in setOf(
            ProjectStructure.GIT_DIRECTORY,
            ProjectStructure.GRADLE_DIRECTORY,
            ProjectStructure.IDEA_DIRECTORY,
            ProjectStructure.KOTLIN_DIRECTORY,
            ProjectStructure.BUILD_DIRECTORY,
            ProjectStructure.OUT_DIRECTORY
        )
    }

    private companion object {
        const val COMPOSABLE_ANNOTATION = "@Composable"

        val composeFunctionNames = setOf(
            ComponentTypes.COMPOSE_TEXT,
            ComponentTypes.COMPOSE_BUTTON,
            ComponentTypes.COMPOSE_ICON_BUTTON,
            ComponentTypes.COMPOSE_OUTLINED_BUTTON,
            ComponentTypes.COMPOSE_TEXT_BUTTON,
            ComponentTypes.COMPOSE_FLOATING_ACTION_BUTTON,
            ComponentTypes.COMPOSE_IMAGE,
            ComponentTypes.COMPOSE_ICON,
            ComponentTypes.COMPOSE_COLUMN,
            ComponentTypes.COMPOSE_ROW,
            ComponentTypes.COMPOSE_BOX,
            ComponentTypes.COMPOSE_LAZY_COLUMN,
            ComponentTypes.COMPOSE_LAZY_ROW,
            ComponentTypes.COMPOSE_SURFACE,
            ComponentTypes.COMPOSE_CARD,
            ComponentTypes.COMPOSE_SPACER
        )
    }
}
