package ru.itis.cli.analysis

import ru.itis.analyzer.Analyzer
import ru.itis.analyzer.rules.base.Rule
import ru.itis.cli.config.RuleMode
import ru.itis.cli.io.CliAnalysisInput
import ru.itis.compose.runtime.RuntimePackageGuard
import ru.itis.compose.rules.ComposeRuleSet
import ru.itis.compose.source.model.ComposeFunction
import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent
import ru.itis.xml.rules.XmlRuleSet
import ru.itis.xml.source.resource.ResourceRepository

class CliAnalysisRunner {

    fun analyze(
        ruleMode: RuleMode,
        input: CliAnalysisInput,
        resourceRepository: ResourceRepository,
        expectedPackageName: String?
    ): List<AnalysisIssue> {
        val staticIssues = analyzeStaticRules(ruleMode, input, resourceRepository)
        val runtimeIssues = analyzeRuntimeRules(ruleMode, input.runtimeComponents, expectedPackageName)
        return staticIssues + runtimeIssues
    }

    private fun analyzeStaticRules(
        ruleMode: RuleMode,
        input: CliAnalysisInput,
        resourceRepository: ResourceRepository
    ): List<AnalysisIssue> {
        return when (ruleMode) {
            RuleMode.ALL,
            RuleMode.STATIC -> analyzeStatic(
                components = input.xmlComponents + input.composeComponents,
                rules = XmlRuleSet.default(resourceRepository) + ComposeRuleSet.staticRules(),
                resourceRepository = resourceRepository,
                composeFunctions = input.composeFunctions
            )
            RuleMode.XML -> analyzeStatic(
                components = input.xmlComponents,
                rules = XmlRuleSet.default(resourceRepository),
                resourceRepository = resourceRepository
            )
            RuleMode.COMPOSE -> analyzeStatic(
                components = input.composeComponents,
                rules = ComposeRuleSet.staticRules(),
                composeFunctions = input.composeFunctions
            )
            RuleMode.RUNTIME -> emptyList()
        }
    }

    private fun analyzeRuntimeRules(
        ruleMode: RuleMode,
        runtimeComponents: List<UiComponent>,
        expectedPackageName: String?
    ): List<AnalysisIssue> {
        if (!ruleMode.includesRuntime) return emptyList()

        val runtimeRules = if (RuntimePackageGuard.hasPackageMismatch(runtimeComponents, expectedPackageName)) {
            ComposeRuleSet.runtimeDiagnosticRules(expectedPackageName)
        } else {
            ComposeRuleSet.runtimeRules(expectedPackageName)
        }
        return Analyzer(rules = runtimeRules).analyze(runtimeComponents)
    }

    private fun analyzeStatic(
        components: List<UiComponent>,
        rules: List<Rule>,
        resourceRepository: ResourceRepository = ResourceRepository.empty(),
        composeFunctions: List<ComposeFunction> = emptyList()
    ): List<AnalysisIssue> {
        return Analyzer(
            resourceRepository = resourceRepository,
            composeFunctions = composeFunctions,
            rules = rules
        ).analyze(components)
    }
}
