package ru.itis.analyzer.rules.compose.api

import ru.itis.analyzer.core.ComposeSourceAnalysisContext
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.ComposeSourceRule
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.source.compose.analyzer.ComposeFunctionRolePredictor
import ru.itis.source.compose.model.ComposeFunction
import ru.itis.source.compose.model.ComposeFunctionRole

class ComposeMissingModifierParameterRule(
    private val rolePredictor: ComposeFunctionRolePredictor = ComposeFunctionRolePredictor()
) : ComposeSourceRule {
    override val id: String = AnalyzerStrings.RuleIds.COMPOSE_MISSING_MODIFIER_PARAMETER

    override fun check(context: ComposeSourceAnalysisContext): List<AnalysisIssue> {
        return context.composeFunctions
            .map { function -> function to rolePredictor.predict(function) }
            .filter { (function, role) -> shouldCheck(function, role) }
            .filterNot { (function, _) -> function.hasModifierParameter }
            .map { (function, role) ->
                AnalysisIssue(
                    ruleId = id,
                    severity = Severity.INFO,
                    componentId = null,
                    componentType = COMPOSABLE_FUNCTION_COMPONENT_TYPE,
                    filePath = function.filePath,
                    message = AnalyzerStrings.Messages.composeMissingModifierParameter(
                        functionName = function.name,
                        predictedRole = role.name
                    ),
                    recommendation = AnalyzerStrings.Messages.COMPOSE_MISSING_MODIFIER_PARAMETER_RECOMMENDATION
                )
            }
    }

    private fun shouldCheck(function: ComposeFunction, role: ComposeFunctionRole): Boolean {
        return role == ComposeFunctionRole.REUSABLE_COMPONENT &&
            !function.hasModifierParameter
    }

    private companion object {
        const val COMPOSABLE_FUNCTION_COMPONENT_TYPE = "ComposableFunction"
    }
}
