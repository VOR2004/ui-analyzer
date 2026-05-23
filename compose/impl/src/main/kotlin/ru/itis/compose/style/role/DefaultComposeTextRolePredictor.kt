package ru.itis.compose.style.role

import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.compose.style.role.values.ComposeTextRolePredictionValues
import ru.itis.compose.style.signature.ComposePredictedTextRole
import ru.itis.model.UiComponent

class DefaultComposeTextRolePredictor : ComposeTextRolePredictor {

    override fun predict(component: UiComponent): ComposePredictedTextRole {
        val typographyStyle = component.properties.typographyStyle.orEmpty()
        val textSize = DimensionUtils.parseSp(component.properties.textSize)
        val textStyle = component.properties.textStyle.orEmpty()
        val values = ComposeTextRolePredictionValues

        return when {
            typographyStyle.containsAny(values.titleTypographyMarkers) -> ComposePredictedTextRole.TITLE
            typographyStyle.containsAny(values.bodyTypographyMarkers) -> ComposePredictedTextRole.BODY
            typographyStyle.containsAny(values.captionTypographyMarkers) -> ComposePredictedTextRole.CAPTION
            textSize != null && textSize <= values.CAPTION_MAX_SP -> ComposePredictedTextRole.CAPTION
            textSize != null && textSize >= values.TITLE_MIN_SP && textStyle.containsBoldMarker() -> {
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
}