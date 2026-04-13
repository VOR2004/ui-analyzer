package ru.itis.style.signature

data class ButtonStyleSignature(
    val background: String?,
    val textColor: String?,
    val textSize: Float?,
    val padding: Float?
) {
    fun isCompleteEnough(): Boolean {
        return background != null || textColor != null || textSize != null || padding != null
    }
}
