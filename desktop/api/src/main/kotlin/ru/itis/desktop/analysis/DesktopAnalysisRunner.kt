package ru.itis.desktop.analysis

interface DesktopAnalysisRunner {
    fun run(request: DesktopAnalysisRequest): DesktopAnalysisResult
}
