package ru.itis.style.extractor

import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.resource.ResourceRepository
import ru.itis.analyzer.utils.ColorUtils
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.UiComponent
import ru.itis.style.signature.ButtonStyleSignature
import ru.itis.style.signature.TextStyleSignature
import ru.itis.style.signature.TextRolePredictor

class StyleFeatureExtractor(
    private val resourceRepository: ResourceRepository = ResourceRepository.empty()
) {
    private val textRolePredictor = TextRolePredictor()

    fun extractFeatures(components: List<UiComponent>): StyleFeatureSet {
        val flatComponents = flattenComponents(components)
        val textViews = flatComponents.filter { component ->
            component.type == ComponentTypes.TEXT_VIEW ||
                component.type.endsWith(ComponentTypes.TEXT_VIEW_SUFFIX)
        }
        val buttons = flatComponents.filter { component ->
            component.type == ComponentTypes.BUTTON ||
                component.type.endsWith(ComponentTypes.BUTTON_SUFFIX) ||
                component.type == ComponentTypes.MATERIAL_BUTTON
        }

        return StyleFeatureSet(
            textSizes = textViews.mapNotNull { component ->
                DimensionUtils.parseSp(resolveDimension(component.properties.textSize))
            },
            paddings = flatComponents.mapNotNull { component ->
                DimensionUtils.parseDp(resolveDimension(component.properties.padding))
            },
            margins = flatComponents.mapNotNull { component ->
                DimensionUtils.parseDp(resolveDimension(component.properties.margin))
            },
            buttonSignatures = buttons
                .mapNotNull { component -> extractButtonStyleSignature(component) },
            textStyleSignatures = textViews
                .mapNotNull { component -> extractTextStyleSignature(component) }
        )
    }

    private fun extractButtonStyleSignature(component: UiComponent): ButtonStyleSignature? {
        val background = resolveColor(component.properties.backgroundTint ?: component.properties.backgroundColor)
        val textColor = resolveColor(component.properties.textColor)
        val textSize = DimensionUtils.parseSp(resolveDimension(component.properties.textSize))
        val padding = DimensionUtils.parseDp(resolveDimension(component.properties.padding))

        val signature = ButtonStyleSignature(
            background = background,
            textColor = textColor,
            textSize = textSize,
            padding = padding
        )

        return signature.takeIf { it.isCompleteEnough() }
    }

    private fun extractTextStyleSignature(component: UiComponent): TextStyleSignature? {
        val textSize = DimensionUtils.parseSp(resolveDimension(component.properties.textSize))
        val text = resourceRepository.resolveString(component.properties.text) ?: component.properties.text
        val signature = TextStyleSignature(
            role = textRolePredictor.predict(
                textSize = textSize,
                text = text,
                textStyle = component.properties.textStyle
            ),
            textSize = textSize,
            textStyle = component.properties.textStyle?.trim()?.ifBlank { null },
            fontFamily = component.properties.fontFamily?.trim()?.ifBlank { null }
        )

        return signature.takeIf { it.isCompleteEnough() }
    }

    private fun flattenComponents(components: List<UiComponent>): List<UiComponent> {
        return ComponentUtils.flattenAll(components)
    }

    private fun resolveColor(value: String?): String? {
        return resourceRepository.resolveColor(value) ?: ColorUtils.extractComparableColor(value)
    }

    private fun resolveDimension(value: String?): String? {
        return resourceRepository.resolveDimension(value) ?: value
    }
}
