package ru.itis.desktop.analysis

import ru.itis.analyzer.rules.base.Rule
import ru.itis.compose.rules.ComposeRuleSet
import ru.itis.xml.rules.XmlRuleSet
import ru.itis.xml.source.resource.ResourceRepository

object RuleCatalog {

    fun descriptors(
        mode: DesktopAnalysisMode,
        staticTarget: StaticSourceTarget
    ): List<RuleDescriptor> {
        return when (mode) {
            DesktopAnalysisMode.STATIC -> staticRuleDescriptors(staticTarget)
            DesktopAnalysisMode.RUNTIME -> runtimeRuleDescriptors()
        }
    }

    fun staticRules(
        resourceRepository: ResourceRepository,
        staticTarget: StaticSourceTarget,
        selectedRuleIds: Set<String>
    ): List<Rule> {
        return buildStaticRules(resourceRepository, staticTarget)
            .filterSelected(selectedRuleIds)
    }

    fun runtimeRules(
        expectedPackageName: String?,
        selectedRuleIds: Set<String>
    ): List<Rule> {
        return ComposeRuleSet.runtimeRules(expectedPackageName)
            .filterSelected(selectedRuleIds)
    }

    private fun staticRuleDescriptors(staticTarget: StaticSourceTarget): List<RuleDescriptor> {
        return buildStaticRules(ResourceRepository.empty(), staticTarget)
            .map { rule ->
                RuleDescriptor(
                    id = rule.id,
                    source = sourceLabel(rule),
                    kind = "Static"
                )
            }
    }

    private fun runtimeRuleDescriptors(): List<RuleDescriptor> {
        return ComposeRuleSet.runtimeRules()
            .map { rule ->
                RuleDescriptor(
                    id = rule.id,
                    source = "Runtime",
                    kind = "Runtime"
                )
            }
    }

    private fun buildStaticRules(
        resourceRepository: ResourceRepository,
        staticTarget: StaticSourceTarget
    ): List<Rule> {
        return when (staticTarget) {
            StaticSourceTarget.XML -> XmlRuleSet.default(resourceRepository)
            StaticSourceTarget.COMPOSE -> ComposeRuleSet.staticRules()
            StaticSourceTarget.BOTH -> XmlRuleSet.default(resourceRepository) + ComposeRuleSet.staticRules()
        }
    }

    private fun List<Rule>.filterSelected(selectedRuleIds: Set<String>): List<Rule> {
        return filter { rule -> rule.id in selectedRuleIds }
    }

    private fun sourceLabel(rule: Rule): String {
        return when {
            rule.id.startsWith("xml-") -> "XML"
            rule.id.startsWith("compose-") -> "Compose"
            else -> "Static"
        }
    }
}
