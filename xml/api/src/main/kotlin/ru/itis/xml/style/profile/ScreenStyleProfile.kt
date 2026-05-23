package ru.itis.xml.style.profile

import ru.itis.style.cluster.DimensionCluster
import ru.itis.xml.style.signature.ButtonStyleSignature
import ru.itis.xml.style.signature.PredictedTextRole
import ru.itis.xml.style.signature.TextStyleSignature

data class ScreenStyleProfile(
    val filePath: String,
    val textSizeClusters: List<DimensionCluster>,
    val textSizeClustersByRole: Map<PredictedTextRole, List<DimensionCluster>>,
    val paddingClusters: List<DimensionCluster>,
    val marginClusters: List<DimensionCluster>,
    val spacingScale: SpacingScale,
    val dominantButtonStyle: ButtonStyleSignature?,
    val dominantTextStyle: TextStyleSignature?,
    val dominantTextStylesByRole: Map<PredictedTextRole, TextStyleSignature>
)
