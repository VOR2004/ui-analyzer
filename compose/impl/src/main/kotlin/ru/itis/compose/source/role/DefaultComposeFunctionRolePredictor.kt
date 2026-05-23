package ru.itis.compose.source.role

import ru.itis.compose.source.role.values.ComposeFunctionRolePredictionValues
import ru.itis.compose.source.model.ComposeFunction
import ru.itis.compose.source.model.ComposeFunctionRole

class DefaultComposeFunctionRolePredictor : ComposeFunctionRolePredictor {

    override fun predict(function: ComposeFunction): ComposeFunctionRole {
        val name = function.name
        val parameters = function.normalizedParameterText
        val body = function.normalizedBodyText
        val values = ComposeFunctionRolePredictionValues

        return when {
            function.isPrivate -> ComposeFunctionRole.INTERNAL_SECTION
            body.containsAnyCall(values.themeBodyCalls) -> ComposeFunctionRole.THEME_OR_PROVIDER
            body.containsAnyCall(values.routeBodyCalls) -> ComposeFunctionRole.ROUTE
            body.containsAnyCall(values.screenBodyCalls) -> ComposeFunctionRole.SCREEN
            body.containsAny(values.screenBodyMarkers) -> ComposeFunctionRole.SCREEN
            function.hasComposableContentParameter -> ComposeFunctionRole.SLOT_WRAPPER
            name.hasAnySuffix(values.themeOrProviderSuffixes) -> ComposeFunctionRole.THEME_OR_PROVIDER
            name.hasAnySuffix(values.routeSuffixes) -> ComposeFunctionRole.ROUTE
            name.hasAnySuffix(values.screenSuffixes) -> ComposeFunctionRole.SCREEN
            parameters.containsAny(values.screenParameterMarkers) -> ComposeFunctionRole.INTERNAL_SECTION
            name.hasAnySuffix(values.internalSectionSuffixes) -> ComposeFunctionRole.INTERNAL_SECTION
            name.hasAnySuffix(values.reusableComponentSuffixes) -> ComposeFunctionRole.REUSABLE_COMPONENT
            name.firstOrNull()?.isUpperCase() == true -> ComposeFunctionRole.UNKNOWN
            else -> ComposeFunctionRole.UNKNOWN
        }
    }

    private fun String.hasAnySuffix(suffixes: Set<String>): Boolean {
        return suffixes.any { suffix -> endsWith(suffix) }
    }

    private fun String.containsAny(markers: Set<String>): Boolean {
        return markers.any { marker -> contains(marker) }
    }

    private fun String.containsAnyCall(names: Set<String>): Boolean {
        return names.any { name ->
            contains("$name(") || contains("$name {")
        }
    }
}