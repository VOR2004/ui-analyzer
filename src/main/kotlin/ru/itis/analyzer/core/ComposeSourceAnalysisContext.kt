package ru.itis.analyzer.core

import ru.itis.compose.source.model.ComposeFunction

data class ComposeSourceAnalysisContext(
    val composeFunctions: List<ComposeFunction>
)
