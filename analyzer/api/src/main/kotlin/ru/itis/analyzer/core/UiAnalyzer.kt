package ru.itis.analyzer.core

import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent

interface UiAnalyzer {
    fun analyze(components: List<UiComponent>): List<AnalysisIssue>
}
