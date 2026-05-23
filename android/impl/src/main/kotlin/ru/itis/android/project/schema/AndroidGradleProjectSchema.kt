package ru.itis.android.project.schema

internal object AndroidGradleProjectSchema {
    const val APPLICATION_ID_PROPERTY = "applicationId"
    const val NAMESPACE_PROPERTY = "namespace"
    const val VALUE_GROUP_INDEX = 1

    val gradleFileNames = setOf("build.gradle", "build.gradle.kts")
    val propertyPatterns = listOf(
        "applicationId\\s*=\\s*\"([^\"]+)\"",
        "applicationId\\s+\"([^\"]+)\"",
        "namespace\\s*=\\s*\"([^\"]+)\"",
        "namespace\\s+\"([^\"]+)\""
    )
}