package ru.itis.desktop.analysis

import ru.itis.analyzer.rules.base.Rule
import ru.itis.compose.rules.ComposeRuleSet
import ru.itis.xml.rules.XmlRuleSet
import ru.itis.xml.source.resource.ResourceRepository

class DefaultDesktopRuleRegistry : DesktopRuleRegistry {

    override fun descriptors(
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
        return buildStaticRuleGroups(ResourceRepository.empty(), staticTarget)
            .flatMap { group ->
                group.rules.map { rule ->
                    RuleDescriptor(
                        id = rule.id,
                        source = group.source,
                        kind = "Static rule"
                    )
                }
            }
    }

    private fun runtimeRuleDescriptors(): List<RuleDescriptor> {
        return ComposeRuleSet.runtimeRules()
            .map { rule ->
                RuleDescriptor(
                    id = rule.id,
                    source = "Device snapshot",
                    kind = "Runtime rule"
                )
            }
    }

    private fun buildStaticRules(
        resourceRepository: ResourceRepository,
        staticTarget: StaticSourceTarget
    ): List<Rule> {
        return buildStaticRuleGroups(resourceRepository, staticTarget)
            .flatMap { group -> group.rules }
    }

    private fun List<Rule>.filterSelected(selectedRuleIds: Set<String>): List<Rule> {
        return filter { rule -> rule.id in selectedRuleIds }
    }

    private fun buildStaticRuleGroups(
        resourceRepository: ResourceRepository,
        staticTarget: StaticSourceTarget
    ): List<RuleGroup> {
        return buildList {
            if (staticTarget == StaticSourceTarget.XML || staticTarget == StaticSourceTarget.BOTH) {
                add(RuleGroup(source = "XML", rules = XmlRuleSet.default(resourceRepository)))
            }
            if (staticTarget == StaticSourceTarget.COMPOSE || staticTarget == StaticSourceTarget.BOTH) {
                add(RuleGroup(source = "Compose", rules = ComposeRuleSet.staticRules()))
            }
        }
    }

    private data class RuleGroup(
        val source: String,
        val rules: List<Rule>
    )
}
