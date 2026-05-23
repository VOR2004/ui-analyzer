package ru.itis.analyzer.rules.base

import ru.itis.analyzer.core.AnalysisContext
import ru.itis.model.AnalysisIssue

interface ContextualRule : Rule {
    fun check(context: AnalysisContext): List<AnalysisIssue>
}
