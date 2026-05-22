package ru.itis.analyzer.rules.base

import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent

interface Rule {
    val id: String
    fun check(components: List<UiComponent>): List<AnalysisIssue>
}
