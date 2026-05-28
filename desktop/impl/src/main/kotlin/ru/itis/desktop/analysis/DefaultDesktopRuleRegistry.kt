package ru.itis.desktop.analysis

import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.rules.base.Rule
import ru.itis.compose.rules.ComposeRuleSet
import ru.itis.desktop.text.DesktopRuleText
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

    fun runtimeDiagnosticRules(
        expectedPackageName: String?,
        selectedRuleIds: Set<String>
    ): List<Rule> {
        val diagnosticRules = ComposeRuleSet.runtimeDiagnosticRules(expectedPackageName)
        val selectedRules = diagnosticRules
            .filterSelected(selectedRuleIds)
        val packageWarningRule = diagnosticRules
            .first { rule -> rule.id == RuleIds.RUNTIME_SYSTEM_APP_SNAPSHOT_WARNING }

        return (selectedRules + packageWarningRule).distinctBy { rule -> rule.id }
    }

    private fun staticRuleDescriptors(staticTarget: StaticSourceTarget): List<RuleDescriptor> {
        return buildStaticRuleGroups(ResourceRepository.empty(), staticTarget)
            .flatMap { group ->
                group.rules.map { rule ->
                    RuleDescriptor(
                        id = rule.id,
                        source = group.source,
                        kind = DesktopRuleText.STATIC_KIND
                    )
                }
            }
    }

    private fun runtimeRuleDescriptors(): List<RuleDescriptor> {
        return ComposeRuleSet.runtimeRules()
            .map { rule ->
                RuleDescriptor(
                    id = rule.id,
                    source = DesktopRuleText.DEVICE_SNAPSHOT_SOURCE,
                    kind = DesktopRuleText.RUNTIME_KIND
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
                add(RuleGroup(source = DesktopRuleText.XML_SOURCE, rules = XmlRuleSet.default(resourceRepository)))
            }
            if (staticTarget == StaticSourceTarget.COMPOSE || staticTarget == StaticSourceTarget.BOTH) {
                add(RuleGroup(source = DesktopRuleText.COMPOSE_SOURCE, rules = ComposeRuleSet.staticRules()))
            }
        }
    }

    private data class RuleGroup(
        val source: String,
        val rules: List<Rule>
    )
}
