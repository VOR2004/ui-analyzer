package ru.itis.compose.helpers.model

data class ComposeNearDuplicateClusterResult(
    val replacements: List<ComposeClusterReplacement> = emptyList(),
    val flaggedKeys: Set<String> = emptySet()
)
