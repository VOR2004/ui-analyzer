package ru.itis.analyzer.utils

import ru.itis.analyzer.config.ComponentTypes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

object ComponentUtils {

    private val composeButtonTypes = setOf(
        ComponentTypes.COMPOSE_BUTTON,
        ComponentTypes.COMPOSE_ICON_BUTTON,
        ComponentTypes.COMPOSE_OUTLINED_BUTTON,
        ComponentTypes.COMPOSE_TEXT_BUTTON,
        ComponentTypes.COMPOSE_FLOATING_ACTION_BUTTON
    )

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
            it.type == ComponentTypes.TEXT_VIEW ||
                    it.type.endsWith(ComponentTypes.TEXT_VIEW_SUFFIX) ||
                    it.type == ComponentTypes.COMPOSE_TEXT
        }
    }

    fun findButtons(components: List<UiComponent>): List<UiComponent> {
        return flattenAll(components).filter(::isButton)
    }

    fun isButton(component: UiComponent): Boolean {
        return when (component.sourceType) {
            SourceType.XML -> isXmlButtonType(component.type)
            SourceType.COMPOSE,
            SourceType.COMPOSE_RUNTIME -> isComposeButtonType(component.type)
            SourceType.ANDROID_RUNTIME -> isXmlButtonType(component.type) || isComposeButtonType(component.type)
        }
    }

    fun isXmlButtonType(type: String): Boolean {
        return type == ComponentTypes.BUTTON ||
                type == ComponentTypes.MATERIAL_BUTTON ||
                type.endsWith(ComponentTypes.BUTTON_SUFFIX)
    }

    fun isComposeButtonType(type: String): Boolean {
        return type in composeButtonTypes
    }

    fun findImageComponents(components: List<UiComponent>): List<UiComponent> {
        return flattenAll(components).filter {
            it.type == ComponentTypes.IMAGE_VIEW ||
                    it.type.endsWith(ComponentTypes.IMAGE_VIEW_SUFFIX) ||
                    it.type == ComponentTypes.IMAGE_BUTTON ||
                    it.type.endsWith(ComponentTypes.IMAGE_BUTTON_SUFFIX) ||
                    it.type == ComponentTypes.COMPOSE_IMAGE ||
                    it.type == ComponentTypes.COMPOSE_ICON
        }
    }
}
