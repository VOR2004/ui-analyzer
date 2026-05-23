package ru.itis.analyzer.utils

object ClusterUtils {

    fun <T> connectedClusters(
        values: List<T>,
        areConnected: (T, T) -> Boolean
    ): List<Set<T>> {
        val adjacency = mutableMapOf<T, MutableSet<T>>()
        values.forEach { value -> adjacency.putIfAbsent(value, mutableSetOf()) }

        for (firstIndex in values.indices) {
            for (secondIndex in firstIndex + 1 until values.size) {
                val first = values[firstIndex]
                val second = values[secondIndex]

                if (areConnected(first, second)) {
                    adjacency.getValue(first).add(second)
                    adjacency.getValue(second).add(first)
                }
            }
        }

        val visited = mutableSetOf<T>()
        val clusters = mutableListOf<Set<T>>()

        for (value in values) {
            if (!visited.add(value)) continue

            val cluster = mutableSetOf<T>()
            val queue = ArrayDeque<T>()
            queue.add(value)

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
}
