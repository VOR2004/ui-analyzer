package ru.itis.compose.style.extractor

import ru.itis.compose.style.signature.ComposeButtonStyleSignature
import ru.itis.compose.style.signature.ComposeTextStyleSignature

data class ComposeStyleFeatureSet(
    val spacingValuesDp: List<Float>,
    val textSizesSp: List<Float>,
    val colorValues: List<String>,
    val textStyleSignatures: List<ComposeTextStyleSignature>,
    val buttonStyleSignatures: List<ComposeButtonStyleSignature>
)
