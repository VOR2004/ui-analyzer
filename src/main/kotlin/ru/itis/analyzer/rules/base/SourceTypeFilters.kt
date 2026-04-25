package ru.itis.analyzer.rules.base

import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

fun List<UiComponent>.onlyXmlRoots(): List<UiComponent> {
    return filter { component -> component.sourceType == SourceType.XML }
}

fun List<UiComponent>.onlyXmlFlatComponents(): List<UiComponent> {
    return ComponentUtils.flattenAll(this)
        .filter { component -> component.sourceType == SourceType.XML }
}
