package ru.itis.compose.style.profile

import ru.itis.compose.style.signature.ComposeButtonStyleSignature
import ru.itis.compose.style.signature.ComposePredictedTextRole
import ru.itis.compose.style.signature.ComposeTextStyleSignature
import ru.itis.style.cluster.DimensionCluster
import ru.itis.xml.style.profile.SpacingScale

data class ComposeScreenStyleProfile(
    val filePath: String,
    val spacingClusters: List<DimensionCluster>,
    val textSizeClusters: List<DimensionCluster>,
    val textSizeClustersByRole: Map<ComposePredictedTextRole, List<DimensionCluster>>,
    val spacingScale: SpacingScale,
    val dominantTextStylesByRole: Map<ComposePredictedTextRole, ComposeTextStyleSignature>,
    val dominantButtonStyle: ComposeButtonStyleSignature?,
    val colorPalette: List<String>
)
