package ru.itis.analyzer

import ru.itis.analyzer.core.AnalysisContext
import ru.itis.analyzer.core.ComposeSourceAnalysisContext
import ru.itis.analyzer.core.UiAnalyzer
import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.analyzer.rules.base.ComposeSourceRule
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.rules.base.Rule
import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent
import ru.itis.compose.source.model.ComposeFunction
import ru.itis.xml.style.extractor.StyleFeatureExtractor
import ru.itis.xml.style.profiler.StyleProfiler

class Analyzer(
    private val rules: List<Rule>,
    private val composeFunctions: List<ComposeFunction> = emptyList(),
    private val resourceRepository: ResourceRepository = ResourceRepository.empty(),
    private val styleProfiler: StyleProfiler = StyleProfiler(
        featureExtractor = StyleFeatureExtractor(resourceRepository)
    )
) : UiAnalyzer {
    override fun analyze(components: List<UiComponent>): List<AnalysisIssue> {
        val screenProfiles = styleProfiler.buildScreenProfiles(components)
        val projectStyleProfile = styleProfiler.buildProjectProfile(components)
        val context = AnalysisContext(
            components = components,
            resourceRepository = resourceRepository,
            projectStyleProfile = projectStyleProfile,
            screenProfiles = screenProfiles
        )
        val composeSourceContext = ComposeSourceAnalysisContext(composeFunctions)

        return rules.flatMap { rule ->
            when (rule) {
                is ComposeSourceRule -> rule.check(composeSourceContext)
                is ContextualRule -> rule.check(context)
                else -> rule.check(components)
            }
        }
    }
}
