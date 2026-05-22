package ru.itis.xml.helpers

import ru.itis.analyzer.config.AnalyzerFormat
import ru.itis.xml.source.resource.ResourceRepository
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

        val clusters = buildColorClusters(uniqueColors, nearThreshold)
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

    fun buildColorClusters(colors: List<String>, nearThreshold: Double): List<Set<String>> {
        val adjacency = mutableMapOf<String, MutableSet<String>>()

        colors.forEach { color ->
            adjacency.putIfAbsent(color, mutableSetOf())
        }

        for (i in colors.indices) {
            for (j in i + 1 until colors.size) {
                val first = colors[i]
                val second = colors[j]

                if (ColorUtils.areColorsClose(first, second, nearThreshold)) {
                    adjacency.getValue(first).add(second)
                    adjacency.getValue(second).add(first)
                }
            }
        }

        val visited = mutableSetOf<String>()
        val clusters = mutableListOf<Set<String>>()

        for (color in colors) {
            if (!visited.add(color)) continue

            val cluster = mutableSetOf<String>()
            val queue = ArrayDeque<String>()
            queue.add(color)

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                cluster.add(current)

                for (neighbor in adjacency[current].orEmpty()) {
                    if (visited.add(neighbor)) {
                        queue.add(neighbor)
                    }
                }
            }

            clusters += cluster
        }

        return clusters
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
