package ru.itis.desktop.analysis

data class DesktopAnalysisResult(
    val componentCount: Int,
    val issueCount: Int,
    val outputPath: String
)
