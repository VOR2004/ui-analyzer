package ru.itis.style.signature

class TextRolePredictor {

    fun predict(
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
            titleScore >= TITLE_SCORE_THRESHOLD && titleScore > captionScore -> PredictedTextRole.TITLE
            captionScore >= CAPTION_SCORE_THRESHOLD && captionScore > titleScore -> PredictedTextRole.CAPTION
            else -> PredictedTextRole.BODY
        }
    }

    private fun buildTitleScore(
        textSize: Float,
        text: String,
        textStyle: String
    ): Int {
        var score = 0

        if (textSize >= 20f) score += 3
        else if (textSize >= 18f) score += 2
        else if (textSize >= 16f) score += 1

        if ("bold" in textStyle) score += 1
        if (text.isNotBlank() && text.length <= 40) score += 1

        return score
    }

    private fun buildCaptionScore(
        textSize: Float,
        text: String
    ): Int {
        var score = 0

        if (textSize <= 12f) score += 3
        else if (textSize <= 13f) score += 2
        else if (textSize <= 14f) score += 1

        if (text.isNotBlank() && text.length <= 60) score += 1

        return score
    }

    private companion object {
        const val TITLE_SCORE_THRESHOLD = 3
        const val CAPTION_SCORE_THRESHOLD = 3
    }
}
