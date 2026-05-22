package ru.itis.report

import kotlinx.serialization.Serializable

@Serializable
data class Summary(
    val totalComponents: Int,
    val totalIssues: Int
)
