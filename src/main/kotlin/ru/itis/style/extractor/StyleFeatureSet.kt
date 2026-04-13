package ru.itis.style.extractor

import ru.itis.style.signature.ButtonStyleSignature
import ru.itis.style.signature.TextStyleSignature

data class StyleFeatureSet(
    val textSizes: List<Float>,
    val paddings: List<Float>,
    val margins: List<Float>,
    val buttonSignatures: List<ButtonStyleSignature>,
    val textStyleSignatures: List<TextStyleSignature>
)
