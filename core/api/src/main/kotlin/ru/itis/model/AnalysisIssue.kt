package ru.itis.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisIssue(
    val ruleId: String,
    val severity: Severity,
    val componentId: String?,
    val componentLocator: String? = null,
    val componentType: String,
    val filePath: String,
    val message: String?,
    val recommendation: String
)

@Serializable
enum class Severity {
    INFO,
    WARNING,
    ERROR
}
