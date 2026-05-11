package ru.itis.android.project

import java.io.File

class AndroidProjectPackageResolver {

    fun resolve(projectRoot: File): String? {
        val gradleFiles = projectRoot
            .walkTopDown()
            .filter { file -> file.isFile && file.name in GRADLE_FILE_NAMES }
            .toList()

        return findProperty(gradleFiles, APPLICATION_ID_PROPERTY)
            ?: findProperty(gradleFiles, NAMESPACE_PROPERTY)
    }

    private fun findProperty(files: List<File>, propertyName: String): String? {
        return files.firstNotNullOfOrNull { file ->
            PROPERTY_PATTERNS.firstNotNullOfOrNull { pattern ->
                pattern
                    .toRegex(RegexOption.MULTILINE)
                    .find(file.readText())
                    ?.groups
                    ?.get(VALUE_GROUP_INDEX)
                    ?.value
                    ?.takeIf { value -> pattern.startsWith(propertyName) }
            }
        }
    }

    private companion object {
        const val APPLICATION_ID_PROPERTY = "applicationId"
        const val NAMESPACE_PROPERTY = "namespace"
        const val VALUE_GROUP_INDEX = 1
        val GRADLE_FILE_NAMES = setOf("build.gradle", "build.gradle.kts")
        val PROPERTY_PATTERNS = listOf(
            "applicationId\\s*=\\s*\"([^\"]+)\"",
            "applicationId\\s+\"([^\"]+)\"",
            "namespace\\s*=\\s*\"([^\"]+)\"",
            "namespace\\s+\"([^\"]+)\""
        )
    }
}
