package ru.itis.cli.io

import ru.itis.cli.config.RuleMode
import ru.itis.compose.source.model.ComposeFunction
import ru.itis.model.UiComponent

data class CliAnalysisInput(
    val xmlComponents: List<UiComponent>,
    val composeComponents: List<UiComponent>,
    val runtimeComponents: List<UiComponent>,
    val composeFunctions: List<ComposeFunction>
) {
    fun componentsForReport(ruleMode: RuleMode): List<UiComponent> {
        return when (ruleMode) {
            RuleMode.ALL -> xmlComponents + composeComponents + runtimeComponents
            RuleMode.STATIC -> xmlComponents + composeComponents
            RuleMode.XML -> xmlComponents
            RuleMode.COMPOSE -> composeComponents
            RuleMode.RUNTIME -> runtimeComponents
        }
    }
}
