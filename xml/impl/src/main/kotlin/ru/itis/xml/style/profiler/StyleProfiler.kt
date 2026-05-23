package ru.itis.xml.style.profiler

import ru.itis.analyzer.utils.DimensionScaleUtils
import ru.itis.model.UiComponent
import ru.itis.style.cluster.DimensionCluster
import ru.itis.style.cluster.DimensionClusterer
import ru.itis.xml.style.extractor.StyleFeatureExtractor
import ru.itis.xml.style.profile.ProjectStyleProfile
import ru.itis.xml.style.profile.ScreenStyleProfile
import ru.itis.xml.style.profile.SpacingScale
import ru.itis.xml.style.signature.ButtonStyleSignature
import ru.itis.xml.style.signature.PredictedTextRole
import ru.itis.xml.style.signature.TextStyleSignature

class StyleProfiler(
    private val dimensionClusterer: DimensionClusterer = DimensionClusterer(),
    private val featureExtractor: StyleFeatureExtractor = StyleFeatureExtractor()
) {

    fun buildProjectProfile(components: List<UiComponent>): ProjectStyleProfile {
        val profileData = buildProfileData(components)

        return ProjectStyleProfile(
            textSizeClusters = profileData.textSizeClusters,
            textSizeClustersByRole = profileData.textSizeClustersByRole,
            paddingClusters = profileData.paddingClusters,
            marginClusters = profileData.marginClusters,
            spacingScale = profileData.spacingScale,
            dominantButtonStyle = profileData.dominantButtonStyle,
            dominantTextStyle = profileData.dominantTextStyle,
            dominantTextStylesByRole = profileData.dominantTextStylesByRole
        )
    }

    fun buildScreenProfiles(components: List<UiComponent>): Map<String, ScreenStyleProfile> {
        return components
            .groupBy { it.filePath }
            .mapValues { (filePath, roots) ->
                val profileData = buildProfileData(roots)

                ScreenStyleProfile(
                    filePath = filePath,
                    textSizeClusters = profileData.textSizeClusters,
                    textSizeClustersByRole = profileData.textSizeClustersByRole,
                    paddingClusters = profileData.paddingClusters,
                    marginClusters = profileData.marginClusters,
                    spacingScale = profileData.spacingScale,
                    dominantButtonStyle = profileData.dominantButtonStyle,
                    dominantTextStyle = profileData.dominantTextStyle,
                    dominantTextStylesByRole = profileData.dominantTextStylesByRole
                )
            }
    }

    private fun buildProfileData(components: List<UiComponent>): ProfileData {
        val features = featureExtractor.extractFeatures(components)

        val textSizeClusters = dimensionClusterer.cluster(features.textSizes, TEXT_SIZE_TOLERANCE_DP)
        val textSizeClustersByRole = buildTextSizeClustersByRole(features.textStyleSignatures)
        val paddingClusters = dimensionClusterer.cluster(features.paddings, SPACING_TOLERANCE_DP)
        val marginClusters = dimensionClusterer.cluster(features.margins, SPACING_TOLERANCE_DP)

        return ProfileData(
            textSizeClusters = textSizeClusters,
            textSizeClustersByRole = textSizeClustersByRole,
            paddingClusters = paddingClusters,
            marginClusters = marginClusters,
            spacingScale = buildSpacingScale(paddingClusters, marginClusters),
            dominantButtonStyle = findDominantButtonStyle(features.buttonSignatures),
            dominantTextStyle = findDominantTextStyle(features.textStyleSignatures),
            dominantTextStylesByRole = findDominantTextStylesByRole(features.textStyleSignatures)
        )
    }

    private fun findDominantButtonStyle(signatures: List<ButtonStyleSignature>): ButtonStyleSignature? {
        return signatures
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.takeIf { it.value > 1 }
            ?.key
    }

    private fun findDominantTextStyle(signatures: List<TextStyleSignature>): TextStyleSignature? {
        return signatures
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.takeIf { it.value > 1 }
            ?.key
    }

    private fun findDominantTextStylesByRole(
        signatures: List<TextStyleSignature>
    ): Map<PredictedTextRole, TextStyleSignature> {
        return signatures
            .filter { it.role != null }
            .groupBy { it.role!! }
            .mapNotNull { (role, roleSignatures) ->
                val dominant = roleSignatures
                    .groupingBy { it }
                    .eachCount()
                    .maxByOrNull { it.value }
                    ?.takeIf { it.value > 1 }
                    ?.key

                dominant?.let { role to it }
            }
            .toMap()
    }

    private fun buildTextSizeClustersByRole(
        signatures: List<TextStyleSignature>
    ): Map<PredictedTextRole, List<DimensionCluster>> {
        return signatures
            .filter { signature -> signature.role != null && signature.textSize != null }
            .groupBy { signature -> signature.role!! }
            .mapValues { (_, roleSignatures) ->
                val sizes = roleSignatures.mapNotNull { signature -> signature.textSize }
                dimensionClusterer.cluster(sizes, TEXT_SIZE_TOLERANCE_DP)
            }
    }

    private fun buildSpacingScale(
        paddingClusters: List<DimensionCluster>,
        marginClusters: List<DimensionCluster>
    ): SpacingScale {
        val commonValues = (paddingClusters + marginClusters)
            .asSequence()
            .filter { it.frequency > MIN_COMMON_CLUSTER_FREQUENCY }
            .sortedByDescending { it.frequency }
            .map { it.representativeValue }
            .distinct()
            .take(MAX_COMMON_SPACING_VALUES)
            .toList()

        return SpacingScale(
            baseUnitDp = DimensionScaleUtils.guessBaseUnit(commonValues),
            commonValuesDp = commonValues,
            dominantSpacingDp = commonValues.firstOrNull()
        )
    }

    private companion object {
        const val TEXT_SIZE_TOLERANCE_DP = 1f
        const val SPACING_TOLERANCE_DP = 2f
        const val MAX_COMMON_SPACING_VALUES = 6
        const val MIN_COMMON_CLUSTER_FREQUENCY = 1
    }

    private data class ProfileData(
        val textSizeClusters: List<DimensionCluster>,
        val textSizeClustersByRole: Map<PredictedTextRole, List<DimensionCluster>>,
        val paddingClusters: List<DimensionCluster>,
        val marginClusters: List<DimensionCluster>,
        val spacingScale: SpacingScale,
        val dominantButtonStyle: ButtonStyleSignature?,
        val dominantTextStyle: TextStyleSignature?,
        val dominantTextStylesByRole: Map<PredictedTextRole, TextStyleSignature>
    )
}
