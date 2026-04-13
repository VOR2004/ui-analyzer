package ru.itis.style.profile

import ru.itis.style.cluster.DimensionCluster
import ru.itis.style.signature.ButtonStyleSignature
import ru.itis.style.signature.PredictedTextRole
import ru.itis.style.signature.TextStyleSignature

data class ProjectStyleProfile(
    val textSizeClusters: List<DimensionCluster>,
    val textSizeClustersByRole: Map<PredictedTextRole, List<DimensionCluster>>,
    val paddingClusters: List<DimensionCluster>,
    val marginClusters: List<DimensionCluster>,
    val spacingScale: SpacingScale,
    val dominantButtonStyle: ButtonStyleSignature?,
    val dominantTextStyle: TextStyleSignature?,
    val dominantTextStylesByRole: Map<PredictedTextRole, TextStyleSignature>
)
