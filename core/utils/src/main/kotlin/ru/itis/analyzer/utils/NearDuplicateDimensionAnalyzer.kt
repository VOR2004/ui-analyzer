package ru.itis.analyzer.utils

object NearDuplicateDimensionAnalyzer {

    fun <Entry, Result> analyze(
        entries: List<Entry>,
        minDistinctValues: Int,
        nearDuplicateDistance: Float,
        valueSelector: (Entry) -> Float,
        resultFactory: (Entry, Float) -> Result
    ): List<Result> {
        val values = entries.map(valueSelector).distinct().sorted()
        if (values.size < minDistinctValues) {
            return emptyList()
        }

        return ClusterUtils.connectedClusters(values) { first, second ->
            isNearDuplicate(first, second, nearDuplicateDistance)
        }
            .filter { cluster -> cluster.size > 1 }
            .flatMap { cluster ->
                val canonicalValue = findCanonicalValue(cluster, entries, valueSelector)
                entries
                    .filter { entry -> valueSelector(entry) in cluster && valueSelector(entry) != canonicalValue }
                    .map { entry -> resultFactory(entry, canonicalValue) }
            }
    }

    private fun isNearDuplicate(first: Float, second: Float, maxDistance: Float): Boolean {
        val distance = kotlin.math.abs(first - second)
        return distance > 0f && distance <= maxDistance
    }

    private fun <Entry> findCanonicalValue(
        cluster: Set<Float>,
        entries: List<Entry>,
        valueSelector: (Entry) -> Float
    ): Float {
        return cluster.maxWith(
            compareBy<Float> { value -> entries.count { entry -> valueSelector(entry) == value } }
                .thenBy { value -> value }
        )
    }
}
