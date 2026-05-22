package ru.itis.analyzer.utils

import ru.itis.model.UiComponent

object GroupingUtils {

    fun groupByFile(components: List<UiComponent>): Map<String, List<UiComponent>> {
        return ComponentUtils
            .flattenAll(components)
            .groupBy { it.filePath }
    }
}