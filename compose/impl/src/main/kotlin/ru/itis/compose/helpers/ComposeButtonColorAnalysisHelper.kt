package ru.itis.compose.helpers

import ru.itis.analyzer.config.analyzer.AnalyzerFormat
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.utils.ColorUtils
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.compose.helpers.model.ComposeButtonColorEntry
import ru.itis.compose.helpers.model.ComposeClusterReplacement
import ru.itis.compose.helpers.model.ComposeNearDuplicateClusterResult
import ru.itis.compose.style.utils.ComposeColorValueNormalizer
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeButtonColorAnalysisHelper {

    fun resolveButtonEntries(components: List<UiComponent>): List<ComposeButtonColorEntry> {
        return ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType == SourceType.COMPOSE }
            .filter { component -> component.type in composeButtonTypes }
            .mapNotNull { button ->
                val color = ComposeColorValueNormalizer.normalize(
                    value = button.properties.backgroundColor,
                    includeThemeTokens = false
                ) ?: return@mapNotNull null
                ComposeButtonColorEntry(button = button, color = color)
            }
    }

    fun findDominantColor(entries: List<ComposeButtonColorEntry>): String? {
        return entries
            .groupBy { entry -> entry.color }
            .maxWithOrNull(
                compareBy<Map.Entry<String, List<ComposeButtonColorEntry>>> { entry -> entry.value.size }
                    .thenByDescending { entry -> entry.key }
            )
            ?.key
    }

    fun findNearDuplicateClusterResult(
        entries: List<ComposeButtonColorEntry>,
        nearThreshold: Double
    ): ComposeNearDuplicateClusterResult {
        val uniqueColors = entries.map { entry -> entry.color }.distinct()
        if (uniqueColors.size < 2) {
            return ComposeNearDuplicateClusterResult()
        }

        val clusters = buildColorClusters(uniqueColors, nearThreshold)
        val flaggedKeys = mutableSetOf<String>()
        val replacements = mutableListOf<ComposeClusterReplacement>()

        for (cluster in clusters) {
            if (cluster.size < 2) continue

            val clusterEntries = entries.filter { entry -> entry.color in cluster }
            val counts = clusterEntries.groupingBy { entry -> entry.color }.eachCount()
            if (counts.size < 2) continue

            val canonicalColor = counts
                .maxWithOrNull(
                    compareBy<Map.Entry<String, Int>> { entry -> entry.value }
                        .thenByDescending { entry -> entry.key }
                )
                ?.key
                ?: continue

            for (entry in clusterEntries) {
                if (entry.color == canonicalColor) continue

                val distance = ColorUtils.colorDistance(entry.color, canonicalColor) ?: continue
                flaggedKeys += entryKey(entry.button)
                replacements += ComposeClusterReplacement(
                    entry = entry,
                    canonicalColor = canonicalColor,
                    distance = distance
                )
            }
        }

        return ComposeNearDuplicateClusterResult(
            replacements = replacements,
            flaggedKeys = flaggedKeys
        )
    }

    fun entryKey(button: UiComponent): String {
        return listOf(button.filePath, button.id, button.treePath, button.type)
            .joinToString(AnalyzerFormat.ENTRY_KEY_DELIMITER)
    }

    fun formatDistance(distance: Double): String {
        return String.format(AnalyzerFormat.LOCALE, AnalyzerFormat.DISTANCE_PATTERN, distance)
    }

    private fun buildColorClusters(colors: List<String>, nearThreshold: Double): List<Set<String>> {
        val adjacency = mutableMapOf<String, MutableSet<String>>()
        colors.forEach { color -> adjacency.putIfAbsent(color, mutableSetOf()) }

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

    private companion object {
        val composeButtonTypes = setOf(
            ComponentTypes.COMPOSE_BUTTON,
            ComponentTypes.COMPOSE_ICON_BUTTON,
            ComponentTypes.COMPOSE_OUTLINED_BUTTON,
            ComponentTypes.COMPOSE_TEXT_BUTTON,
            ComponentTypes.COMPOSE_FLOATING_ACTION_BUTTON
        )

    }
}
