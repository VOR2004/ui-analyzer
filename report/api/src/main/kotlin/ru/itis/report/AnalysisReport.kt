package ru.itis.report

import kotlinx.serialization.Serializable
import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent

@Serializable
data class AnalysisReport(
    val summary: Summary,
    val components: List<UiComponent>,
    val issues: List<AnalysisIssue>
)
