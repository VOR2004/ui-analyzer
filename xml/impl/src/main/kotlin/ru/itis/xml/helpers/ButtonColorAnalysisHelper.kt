package ru.itis.xml.helpers

import ru.itis.analyzer.config.analyzer.AnalyzerFormat
import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.analyzer.utils.ClusterUtils
import ru.itis.analyzer.utils.ColorUtils
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.UiComponent

class ButtonColorAnalysisHelper(
    private val resourceRepository: ResourceRepository
) {

    fun resolveButtonEntries(components: List<UiComponent>): List<ButtonColorEntry> {
        return ComponentUtils.findButtons(components)
            .mapNotNull { button ->
                val resolvedColor = resolveButtonColor(button) ?: return@mapNotNull null
                ButtonColorEntry(button, resolvedColor)
            }
    }

    fun resolveButtonEntriesFromFlatComponents(components: List<UiComponent>): List<ButtonColorEntry> {
        return components
            .filter(ComponentUtils::isButton)
            .mapNotNull { button ->
                val resolvedColor = resolveButtonColor(button) ?: return@mapNotNull null
                ButtonColorEntry(button, resolvedColor)
            }
    }

    fun resolveButtonColor(button: UiComponent): String? {
        val candidate = button.properties.backgroundTint
            ?: button.properties.backgroundColor
            ?: return null

        return resourceRepository.resolveColor(candidate)
    }

    fun findDominantColor(entries: List<ButtonColorEntry>): String? {
        return entries
            .groupBy { it.color }
            .maxWithOrNull(
                compareBy<Map.Entry<String, List<ButtonColorEntry>>> { it.value.size }
                    .thenByDescending { it.key }
            )
            ?.key
    }

    fun findNearDuplicateClusterResult(
        entries: List<ButtonColorEntry>,
        nearThreshold: Double
    ): NearDuplicateClusterResult {
        val uniqueColors = entries.map { it.color }.distinct()
        if (uniqueColors.size < 2) {
            return NearDuplicateClusterResult()
        }

        val clusters = ClusterUtils.connectedClusters(uniqueColors) { first, second ->
            ColorUtils.areColorsClose(first, second, nearThreshold)
        }
        val flaggedKeys = mutableSetOf<String>()
        val pairs = mutableListOf<ClusterReplacement>()

        for (cluster in clusters) {
            if (cluster.size < 2) continue

            val clusterEntries = entries.filter { it.color in cluster }
            val counts = clusterEntries.groupingBy { it.color }.eachCount()
            if (counts.size < 2) continue

            val canonicalColor = counts
                .maxWithOrNull(
                    compareBy<Map.Entry<String, Int>> { it.value }
                        .thenByDescending { it.key }
                )
                ?.key
                ?: continue

            for (entry in clusterEntries) {
                if (entry.color == canonicalColor) continue

                val distance = ColorUtils.colorDistance(entry.color, canonicalColor) ?: continue
                flaggedKeys += entryKey(entry.button)

                pairs += ClusterReplacement(
                    entry = entry,
                    canonicalColor = canonicalColor,
                    distance = distance
                )
            }
        }

        return NearDuplicateClusterResult(
            replacements = pairs,
            flaggedKeys = flaggedKeys
        )
    }

    fun entryKey(button: UiComponent): String {
        return listOf(button.filePath, button.id, button.type)
            .joinToString(AnalyzerFormat.ENTRY_KEY_DELIMITER)
    }

    fun formatDistance(distance: Double): String {
        return String.format(AnalyzerFormat.LOCALE, AnalyzerFormat.DISTANCE_PATTERN, distance)
    }
}

data class ButtonColorEntry(
    val button: UiComponent,
    val color: String
)

data class ClusterReplacement(
    val entry: ButtonColorEntry,
    val canonicalColor: String,
    val distance: Double
)

data class NearDuplicateClusterResult(
    val replacements: List<ClusterReplacement> = emptyList(),
    val flaggedKeys: Set<String> = emptySet()
)
