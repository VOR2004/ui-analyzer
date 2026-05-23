package ru.itis.android.project

import ru.itis.android.project.schema.AndroidGradleProjectSchema
import java.io.File

class AndroidProjectPackageResolver : AndroidPackageResolver {

    override fun resolve(projectRoot: File): String? {
        val gradleFiles = projectRoot
            .walkTopDown()
            .filter { file -> file.isFile && file.name in AndroidGradleProjectSchema.gradleFileNames }
            .toList()

        return findProperty(gradleFiles, AndroidGradleProjectSchema.APPLICATION_ID_PROPERTY)
            ?: findProperty(gradleFiles, AndroidGradleProjectSchema.NAMESPACE_PROPERTY)
    }

    private fun findProperty(files: List<File>, propertyName: String): String? {
        return files.firstNotNullOfOrNull { file ->
            AndroidGradleProjectSchema.propertyPatterns.firstNotNullOfOrNull { pattern ->
                pattern
                    .toRegex(RegexOption.MULTILINE)
                    .find(file.readText())
                    ?.groups
                    ?.get(AndroidGradleProjectSchema.VALUE_GROUP_INDEX)
                    ?.value
                    ?.takeIf { _ -> pattern.startsWith(propertyName) }
            }
        }
    }
}
