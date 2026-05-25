package ru.itis.desktop.analysis

interface DesktopRuleRegistry {
    fun descriptors(
        mode: DesktopAnalysisMode,
        staticTarget: StaticSourceTarget
    ): List<RuleDescriptor>
}
