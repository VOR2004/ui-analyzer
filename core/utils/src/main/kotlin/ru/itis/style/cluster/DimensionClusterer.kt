package ru.itis.style.cluster

import kotlin.math.abs

class DimensionClusterer {

    fun cluster(values: List<Float>, tolerance: Float): List<DimensionCluster> {
        if (values.isEmpty()) {
            return emptyList()
        }

        val groups = mutableListOf<MutableList<Float>>()

        for (value in values.sorted()) {
            val existingGroup = groups.firstOrNull { group ->
                abs(group.average().toFloat() - value) <= tolerance
            }

            if (existingGroup != null) {
                existingGroup += value
            } else {
                groups += mutableListOf(value)
            }
        }

        return groups
            .map { group ->
                val centroid = group.average().toFloat()
                DimensionCluster(
                    centroid = centroid,
                    representativeValue = group.minByOrNull { value ->
                        abs(value - centroid)
                    } ?: centroid,
                    members = group.toList(),
                    frequency = group.size
                )
            }
            .sortedByDescending { it.frequency }
    }
}
