package ru.itis.compose.style.role.values

internal object ComposeTextRolePredictionValues {
    const val CAPTION_MAX_SP = 13f
    const val TITLE_MIN_SP = 20f

    val titleTypographyMarkers = setOf(
        "typography.title",
        "typography.headline",
        "typography.display"
    )

    val bodyTypographyMarkers = setOf(
        "typography.body"
    )

    val captionTypographyMarkers = setOf(
        "typography.label",
        "typography.caption"
    )
}