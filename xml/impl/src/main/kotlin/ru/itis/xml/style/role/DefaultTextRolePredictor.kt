package ru.itis.xml.style.role

import ru.itis.xml.style.role.values.TextRolePredictionValues
import ru.itis.xml.style.signature.PredictedTextRole
import ru.itis.xml.style.signature.TextRolePredictor

class DefaultTextRolePredictor : TextRolePredictor {

    override fun predict(
        textSize: Float?,
        text: String?,
        textStyle: String?
    ): PredictedTextRole? {
        if (textSize == null) {
            return null
        }

        val normalizedText = text?.trim().orEmpty()
        val normalizedStyle = textStyle?.trim()?.lowercase().orEmpty()

        val titleScore = buildTitleScore(textSize, normalizedText, normalizedStyle)
        val captionScore = buildCaptionScore(textSize, normalizedText)

        return when {
            titleScore >= TextRolePredictionValues.TITLE_SCORE_THRESHOLD &&
                titleScore > captionScore -> PredictedTextRole.TITLE
            captionScore >= TextRolePredictionValues.CAPTION_SCORE_THRESHOLD &&
                captionScore > titleScore -> PredictedTextRole.CAPTION
            else -> PredictedTextRole.BODY
        }
    }

    private fun buildTitleScore(
        textSize: Float,
        text: String,
        textStyle: String
    ): Int {
        var score = 0

        if (textSize >= TextRolePredictionValues.TITLE_STRONG_SIZE_SP) score += 3
        else if (textSize >= TextRolePredictionValues.TITLE_MEDIUM_SIZE_SP) score += 2
        else if (textSize >= TextRolePredictionValues.TITLE_WEAK_SIZE_SP) score += 1

        if ("bold" in textStyle) score += 1
        if (text.isNotBlank() && text.length <= TextRolePredictionValues.TITLE_MAX_TEXT_LENGTH) score += 1

        return score
    }

    private fun buildCaptionScore(
        textSize: Float,
        text: String
    ): Int {
        var score = 0

        if (textSize <= TextRolePredictionValues.CAPTION_STRONG_SIZE_SP) score += 3
        else if (textSize <= TextRolePredictionValues.CAPTION_MEDIUM_SIZE_SP) score += 2
        else if (textSize <= TextRolePredictionValues.CAPTION_WEAK_SIZE_SP) score += 1

        if (text.isNotBlank() && text.length <= TextRolePredictionValues.CAPTION_MAX_TEXT_LENGTH) score += 1

        return score
    }
}
