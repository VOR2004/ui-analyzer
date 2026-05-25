package ru.itis.desktop.text

internal object DesktopPanelText {
    const val BROWSE = "..."
    const val RULE_DRAWER = "Rule drawer"
    const val ANALYSIS_WORKSPACE = "Analysis workspace"
    const val STATUS = "Status"
    const val RUN_ANALYSIS = "Run analysis"

    fun selectedRules(selected: Int, total: Int): String {
        return "Selected rules: $selected of $total"
    }

    fun selectedRulesShort(selected: Int, total: Int): String {
        return "Selected $selected of $total."
    }

    fun selectedSourceRules(selected: Int, total: Int): String {
        return "$selected/$total"
    }

    fun ruleSubtitle(source: String, kind: String): String {
        return "$source - $kind"
    }
}
