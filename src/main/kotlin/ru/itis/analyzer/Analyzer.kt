package ru.itis.analyzer

import ru.itis.analyzer.core.AnalysisContext
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.rules.base.Rule
import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent
import ru.itis.style.profiler.StyleProfiler

class Analyzer(
    private val rules: List<Rule>,
    private val styleProfiler: StyleProfiler = StyleProfiler()
) {
    fun analyze(components: List<UiComponent>): List<AnalysisIssue> {
        val screenProfiles = styleProfiler.buildScreenProfiles(components)
        val projectStyleProfile = styleProfiler.buildProjectProfile(components)
        val context = AnalysisContext(
            components = components,
            projectStyleProfile = projectStyleProfile,
            screenProfiles = screenProfiles
        )

        return rules.flatMap { rule ->
            when (rule) {
                is ContextualRule -> rule.check(context)
                else -> rule.check(components)
            }
        }
    }
}
