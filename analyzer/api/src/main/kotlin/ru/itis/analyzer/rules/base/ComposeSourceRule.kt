package ru.itis.analyzer.rules.base

import ru.itis.analyzer.core.ComposeSourceAnalysisContext
import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent

interface ComposeSourceRule : Rule {
    fun check(context: ComposeSourceAnalysisContext): List<AnalysisIssue>

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return emptyList()
    }
}
