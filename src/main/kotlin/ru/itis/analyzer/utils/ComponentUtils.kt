package ru.itis.analyzer.utils

import ru.itis.analyzer.config.ComponentTypes
import ru.itis.model.UiComponent

object ComponentUtils {

    fun flatten(component: UiComponent): List<UiComponent> {
        return listOf(component) + component.children.flatMap { flatten(it) }
    }

    fun flattenAll(components: List<UiComponent>): List<UiComponent> {
        return components.flatMap { flatten(it) }
    }

    fun findByType(components: List<UiComponent>, type: String): List<UiComponent> {
        return flattenAll(components).filter { it.type == type }
    }

    fun findTextViews(components: List<UiComponent>): List<UiComponent> {
        return flattenAll(components).filter {
            it.type == ComponentTypes.TEXT_VIEW || it.type.endsWith(ComponentTypes.TEXT_VIEW_SUFFIX)
        }
    }

    fun findButtons(components: List<UiComponent>): List<UiComponent> {
        return flattenAll(components).filter {
            it.type == ComponentTypes.BUTTON ||
                    it.type.endsWith(ComponentTypes.BUTTON_SUFFIX) ||
                    it.type == ComponentTypes.MATERIAL_BUTTON
        }
    }

    fun findImageComponents(components: List<UiComponent>): List<UiComponent> {
        return flattenAll(components).filter {
            it.type == ComponentTypes.IMAGE_VIEW ||
                    it.type.endsWith(ComponentTypes.IMAGE_VIEW_SUFFIX) ||
                    it.type == ComponentTypes.IMAGE_BUTTON ||
                    it.type.endsWith(ComponentTypes.IMAGE_BUTTON_SUFFIX)
        }
    }
}
