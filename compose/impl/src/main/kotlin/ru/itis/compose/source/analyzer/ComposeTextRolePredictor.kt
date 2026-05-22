package ru.itis.compose.source.analyzer

import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.UiComponent
import ru.itis.compose.style.signature.ComposePredictedTextRole

class ComposeTextRolePredictor {

    fun predict(component: UiComponent): ComposePredictedTextRole {
        val typographyStyle = component.properties.typographyStyle.orEmpty()
        val textSize = DimensionUtils.parseSp(component.properties.textSize)
        val textStyle = component.properties.textStyle.orEmpty()

        return when {
            typographyStyle.containsAny(titleTypographyMarkers) -> ComposePredictedTextRole.TITLE
            typographyStyle.containsAny(bodyTypographyMarkers) -> ComposePredictedTextRole.BODY
            typographyStyle.containsAny(captionTypographyMarkers) -> ComposePredictedTextRole.CAPTION
            textSize != null && textSize <= CAPTION_MAX_SP -> ComposePredictedTextRole.CAPTION
            textSize != null && textSize >= TITLE_MIN_SP && textStyle.containsBoldMarker() -> {
                ComposePredictedTextRole.TITLE
            }
            else -> ComposePredictedTextRole.BODY
        }
    }

    private fun String.containsAny(markers: Set<String>): Boolean {
        return markers.any { marker -> contains(marker, ignoreCase = true) }
    }

    private fun String.containsBoldMarker(): Boolean {
        return contains("Bold", ignoreCase = true) || contains("SemiBold", ignoreCase = true)
    }

    private companion object {
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
}
