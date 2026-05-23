package ru.itis.compose.style.profiler

import ru.itis.compose.style.extractor.ComposeStyleFeatureExtractor
import ru.itis.compose.style.profile.ComposeProjectStyleProfile
import ru.itis.compose.style.profile.ComposeScreenStyleProfile
import ru.itis.compose.style.signature.ComposeButtonStyleSignature
import ru.itis.compose.style.signature.ComposePredictedTextRole
import ru.itis.compose.style.signature.ComposeTextStyleSignature
import ru.itis.model.UiComponent
import ru.itis.style.cluster.DimensionCluster
import ru.itis.style.cluster.DimensionClusterer
import ru.itis.xml.style.profile.SpacingScale

class ComposeStyleProfiler(
    private val dimensionClusterer: DimensionClusterer = DimensionClusterer(),
    private val featureExtractor: ComposeStyleFeatureExtractor = ComposeStyleFeatureExtractor()
) {

    fun buildProjectProfile(components: List<UiComponent>): ComposeProjectStyleProfile {
        val profileData = buildProfileData(components)

        return ComposeProjectStyleProfile(
            spacingClusters = profileData.spacingClusters,
            textSizeClusters = profileData.textSizeClusters,
            textSizeClustersByRole = profileData.textSizeClustersByRole,
            spacingScale = profileData.spacingScale,
            dominantTextStylesByRole = profileData.dominantTextStylesByRole,
            dominantButtonStyle = profileData.dominantButtonStyle,
            colorPalette = profileData.colorPalette
        )
    }

    fun buildScreenProfiles(components: List<UiComponent>): Map<String, ComposeScreenStyleProfile> {
        return components
            .groupBy { component -> component.filePath }
            .mapValues { (filePath, roots) ->
                val profileData = buildProfileData(roots)

                ComposeScreenStyleProfile(
                    filePath = filePath,
                    spacingClusters = profileData.spacingClusters,
                    textSizeClusters = profileData.textSizeClusters,
                    textSizeClustersByRole = profileData.textSizeClustersByRole,
                    spacingScale = profileData.spacingScale,
                    dominantTextStylesByRole = profileData.dominantTextStylesByRole,
                    dominantButtonStyle = profileData.dominantButtonStyle,
                    colorPalette = profileData.colorPalette
                )
            }
    }

    private fun buildProfileData(components: List<UiComponent>): ProfileData {
        val features = featureExtractor.extractFeatures(components)
        val spacingClusters = dimensionClusterer.cluster(features.spacingValuesDp, SPACING_TOLERANCE_DP)
        val textSizeClusters = dimensionClusterer.cluster(features.textSizesSp, TEXT_SIZE_TOLERANCE_SP)
        val textSizeClustersByRole = buildTextSizeClustersByRole(features.textStyleSignatures)

        return ProfileData(
            spacingClusters = spacingClusters,
            textSizeClusters = textSizeClusters,
            textSizeClustersByRole = textSizeClustersByRole,
            spacingScale = buildSpacingScale(spacingClusters),
            dominantTextStylesByRole = findDominantTextStylesByRole(features.textStyleSignatures),
            dominantButtonStyle = findDominantButtonStyle(features.buttonStyleSignatures),
            colorPalette = buildColorPalette(features.colorValues)
        )
    }

    private fun buildTextSizeClustersByRole(
        signatures: List<ComposeTextStyleSignature>
    ): Map<ComposePredictedTextRole, List<DimensionCluster>> {
        return signatures
            .filter { signature -> signature.textSize != null }
            .groupBy { signature -> signature.role }
            .mapValues { (_, roleSignatures) ->
                dimensionClusterer.cluster(
                    values = roleSignatures.mapNotNull { signature -> signature.textSize },
                    tolerance = TEXT_SIZE_TOLERANCE_SP
                )
            }
    }

    private fun findDominantTextStylesByRole(
        signatures: List<ComposeTextStyleSignature>
    ): Map<ComposePredictedTextRole, ComposeTextStyleSignature> {
        return signatures
            .groupBy { signature -> signature.role }
            .mapNotNull { (role, roleSignatures) ->
                val dominant = roleSignatures
                    .groupingBy { signature -> signature }
                    .eachCount()
                    .maxByOrNull { (_, count) -> count }
                    ?.takeIf { (_, count) -> count > MIN_DOMINANT_STYLE_FREQUENCY }
                    ?.key

                dominant?.let { style -> role to style }
            }
            .toMap()
    }

    private fun findDominantButtonStyle(
        signatures: List<ComposeButtonStyleSignature>
    ): ComposeButtonStyleSignature? {
        return signatures
            .groupingBy { signature -> signature }
            .eachCount()
            .maxByOrNull { (_, count) -> count }
            ?.takeIf { (_, count) -> count > MIN_DOMINANT_STYLE_FREQUENCY }
            ?.key
    }

    private fun buildColorPalette(colors: List<String>): List<String> {
        return colors
            .groupingBy { color -> color }
            .eachCount()
            .entries
            .sortedWith(
                compareByDescending<Map.Entry<String, Int>> { entry -> entry.value }
                    .thenBy { entry -> entry.key }
            )
            .map { entry -> entry.key }
    }

    private fun buildSpacingScale(spacingClusters: List<DimensionCluster>): SpacingScale {
        val commonValues = spacingClusters
            .asSequence()
            .filter { cluster -> cluster.frequency > MIN_COMMON_CLUSTER_FREQUENCY }
            .sortedByDescending { cluster -> cluster.frequency }
            .map { cluster -> cluster.representativeValue }
            .distinct()
            .take(MAX_COMMON_SPACING_VALUES)
            .toList()

        return SpacingScale(
            baseUnitDp = guessBaseUnit(commonValues),
            commonValuesDp = commonValues,
            dominantSpacingDp = commonValues.firstOrNull()
        )
    }

    private fun guessBaseUnit(values: List<Float>): Float? {
        if (values.isEmpty()) {
            return null
        }

        return BASE_UNIT_CANDIDATES.firstOrNull { unit ->
            values.count { value ->
                val remainder = value % unit
                remainder <= BASE_UNIT_EPSILON || remainder >= unit - BASE_UNIT_EPSILON
            } >= values.size / MIN_MATCH_DIVISOR
        }
    }

    private data class ProfileData(
        val spacingClusters: List<DimensionCluster>,
        val textSizeClusters: List<DimensionCluster>,
        val textSizeClustersByRole: Map<ComposePredictedTextRole, List<DimensionCluster>>,
        val spacingScale: SpacingScale,
        val dominantTextStylesByRole: Map<ComposePredictedTextRole, ComposeTextStyleSignature>,
        val dominantButtonStyle: ComposeButtonStyleSignature?,
        val colorPalette: List<String>
    )

    private companion object {
        const val TEXT_SIZE_TOLERANCE_SP = 1f
        const val SPACING_TOLERANCE_DP = 2f
        const val MAX_COMMON_SPACING_VALUES = 6
        const val BASE_UNIT_EPSILON = 0.1f
        const val MIN_MATCH_DIVISOR = 2
        const val MIN_COMMON_CLUSTER_FREQUENCY = 1
        const val MIN_DOMINANT_STYLE_FREQUENCY = 1
        val BASE_UNIT_CANDIDATES = listOf(4f, 8f, 2f)
    }
}
