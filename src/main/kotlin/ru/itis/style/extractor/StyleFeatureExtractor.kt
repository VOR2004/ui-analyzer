package ru.itis.style.extractor

import ru.itis.analyzer.utils.ColorUtils
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.UiComponent
import ru.itis.style.signature.ButtonStyleSignature
import ru.itis.style.signature.TextStyleSignature
import ru.itis.style.signature.TextRolePredictor

class StyleFeatureExtractor {
    private val textRolePredictor = TextRolePredictor()

    fun extractFeatures(components: List<UiComponent>): StyleFeatureSet {
        val flatComponents = flattenComponents(components)

        return StyleFeatureSet(
            textSizes = flatComponents.mapNotNull { component ->
                DimensionUtils.parseSp(component.properties.textSize)
            },
            paddings = flatComponents.mapNotNull { component ->
                DimensionUtils.parseDp(component.properties.padding)
            },
            margins = flatComponents.mapNotNull { component ->
                DimensionUtils.parseDp(component.properties.margin)
            },
            buttonSignatures = ComponentUtils.findButtons(flatComponents)
                .mapNotNull { component -> extractButtonStyleSignature(component) },
            textStyleSignatures = ComponentUtils.findTextViews(flatComponents)
                .mapNotNull { component -> extractTextStyleSignature(component) }
        )
    }

    private fun extractButtonStyleSignature(component: UiComponent): ButtonStyleSignature? {
        val background = ColorUtils.extractComparableColor(
            component.properties.backgroundTint ?: component.properties.backgroundColor
        )
        val textColor = ColorUtils.extractComparableColor(component.properties.textColor)
        val textSize = DimensionUtils.parseSp(component.properties.textSize)
        val padding = DimensionUtils.parseDp(component.properties.padding)

        val signature = ButtonStyleSignature(
            background = background,
            textColor = textColor,
            textSize = textSize,
            padding = padding
        )

        return signature.takeIf { it.isCompleteEnough() }
    }

    private fun extractTextStyleSignature(component: UiComponent): TextStyleSignature? {
        val textSize = DimensionUtils.parseSp(component.properties.textSize)
        val signature = TextStyleSignature(
            role = textRolePredictor.predict(
                textSize = textSize,
                text = component.properties.text,
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
}
