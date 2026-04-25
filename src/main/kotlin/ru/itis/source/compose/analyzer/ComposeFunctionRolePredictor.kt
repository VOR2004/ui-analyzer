package ru.itis.source.compose.analyzer

import ru.itis.source.compose.model.ComposeFunction
import ru.itis.source.compose.model.ComposeFunctionRole

class ComposeFunctionRolePredictor {

    fun predict(function: ComposeFunction): ComposeFunctionRole {
        val name = function.name
        val parameters = function.normalizedParameterText

        return when {
            function.isPrivate -> ComposeFunctionRole.INTERNAL_SECTION
            function.hasComposableContentParameter -> ComposeFunctionRole.SLOT_WRAPPER
            name.hasAnySuffix(themeOrProviderSuffixes) -> ComposeFunctionRole.THEME_OR_PROVIDER
            name.hasAnySuffix(routeSuffixes) -> ComposeFunctionRole.ROUTE
            name.hasAnySuffix(screenSuffixes) -> ComposeFunctionRole.SCREEN
            parameters.containsAny(screenParameterMarkers) -> ComposeFunctionRole.INTERNAL_SECTION
            name.hasAnySuffix(internalSectionSuffixes) -> ComposeFunctionRole.INTERNAL_SECTION
            name.hasAnySuffix(reusableComponentSuffixes) -> ComposeFunctionRole.REUSABLE_COMPONENT
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

    private companion object {
        val themeOrProviderSuffixes = setOf(
            "Theme",
            "Provider"
        )

        val routeSuffixes = setOf(
            "Route",
            "Navigation",
            "NavGraph"
        )

        val screenSuffixes = setOf(
            "Screen",
            "Page"
        )

        val internalSectionSuffixes = setOf(
            "Content",
            "TopBar",
            "BottomBar",
            "Header",
            "Footer",
            "Actions",
            "Section"
        )

        val reusableComponentSuffixes = setOf(
            "Card",
            "Button",
            "Item",
            "Row",
            "Tile",
            "Toolbar",
            "Avatar",
            "Text",
            "Field",
            "Chip",
            "Carousel",
            "Option",
            "Options"
        )

        val screenParameterMarkers = setOf(
            "State",
            "Scope",
            "AnimatedContentScope",
            "SharedTransitionScope",
            "NavController",
            "ViewModel"
        )
    }
}
