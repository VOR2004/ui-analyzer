package ru.itis.desktop.analysis

import ru.itis.model.AnalysisIssue

data class DesktopAnalysisResult(
    val componentCount: Int,
    val issueCount: Int,
    val outputPath: String,
    val issues: List<AnalysisIssue> = emptyList()
)
