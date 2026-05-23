package ru.itis.xml.style.signature

interface TextRolePredictor {

    fun predict(
        textSize: Float?,
        text: String?,
        textStyle: String?
    ): PredictedTextRole?
}
