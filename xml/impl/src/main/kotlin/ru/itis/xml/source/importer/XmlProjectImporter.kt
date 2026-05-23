package ru.itis.xml.source.importer

import ru.itis.analyzer.config.components.ProjectStructure
import java.io.File

class XmlProjectImporter {

    fun findLayoutXmlFiles(projectRoot: File): List<File> {
        if (!projectRoot.exists() || !projectRoot.isDirectory) {
            return emptyList()
        }

        return projectRoot
            .walkTopDown()
            .onEnter { directory -> !shouldSkipDirectory(directory) }
            .filter { file ->
                file.isFile &&
                file.extension == ProjectStructure.XML_EXTENSION &&
                isLayoutDirectory(file.parentFile)
            }
            .sortedBy { it.absolutePath }
            .toList()
    }

    private fun isLayoutDirectory(directory: File?): Boolean {
        val name = directory?.name ?: return false
        return name == ProjectStructure.LAYOUT_DIRECTORY ||
                name.startsWith(ProjectStructure.LAYOUT_DIRECTORY_PREFIX)
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
}
