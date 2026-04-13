package ru.itis.style.cluster

data class DimensionCluster(
    val centroid: Float,
    val representativeValue: Float,
    val members: List<Float>,
    val frequency: Int
)
