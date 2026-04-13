package ru.itis.style.signature

data class TextStyleSignature(
    val role: PredictedTextRole?,
    val textSize: Float?,
    val textStyle: String?,
    val fontFamily: String?
) {
    fun isCompleteEnough(): Boolean {
        return role != null || textSize != null || textStyle != null || fontFamily != null
    }
}
