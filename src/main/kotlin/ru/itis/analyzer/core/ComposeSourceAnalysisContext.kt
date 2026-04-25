package ru.itis.analyzer.core

import ru.itis.source.compose.model.ComposeFunction

data class ComposeSourceAnalysisContext(
    val composeFunctions: List<ComposeFunction>
)
