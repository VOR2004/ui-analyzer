package ru.itis.compose.source.role.values

internal object ComposeFunctionRolePredictionValues {
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

    val themeBodyCalls = setOf(
        "MaterialTheme",
        "CompositionLocalProvider"
    )

    val routeBodyCalls = setOf(
        "NavHost",
        "composable"
    )

    val screenBodyCalls = setOf("Scaffold")

    val screenBodyMarkers = setOf(
        "collectAsState",
        "collectAsStateWithLifecycle",
        "eventSink("
    )
}