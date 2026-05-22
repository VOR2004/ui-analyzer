package ru.itis.compose.rules.api
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.core.ComposeSourceAnalysisContext
import ru.itis.analyzer.rules.base.ComposeSourceRule
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.compose.source.analyzer.ComposeFunctionRolePredictor
import ru.itis.compose.source.model.ComposeFunction
import ru.itis.compose.source.model.ComposeFunctionRole

class ComposeMissingModifierParameterRule(
    private val rolePredictor: ComposeFunctionRolePredictor = ComposeFunctionRolePredictor()
) : ComposeSourceRule {
    override val id: String = RuleIds.COMPOSE_MISSING_MODIFIER_PARAMETER

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
                    message = AnalyzerMessages.composeMissingModifierParameter(
                        functionName = function.name,
                        predictedRole = role.name
                    ),
                    recommendation = AnalyzerMessages.COMPOSE_MISSING_MODIFIER_PARAMETER_RECOMMENDATION
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