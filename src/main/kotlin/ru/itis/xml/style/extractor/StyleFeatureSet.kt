package ru.itis.xml.style.extractor

import ru.itis.xml.style.signature.ButtonStyleSignature
import ru.itis.xml.style.signature.TextStyleSignature

data class StyleFeatureSet(
    val textSizes: List<Float>,
    val paddings: List<Float>,
    val margins: List<Float>,
    val buttonSignatures: List<ButtonStyleSignature>,
    val textStyleSignatures: List<TextStyleSignature>
)
