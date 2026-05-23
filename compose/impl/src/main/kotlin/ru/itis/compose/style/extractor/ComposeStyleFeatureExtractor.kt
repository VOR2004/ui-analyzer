package ru.itis.compose.style.extractor

import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.compose.style.role.ComposeTextRolePredictor
import ru.itis.compose.style.role.DefaultComposeTextRolePredictor
import ru.itis.compose.style.utils.ComposeColorValueNormalizer
import ru.itis.compose.style.signature.ComposeButtonStyleSignature
import ru.itis.compose.style.signature.ComposeTextStyleSignature
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeStyleFeatureExtractor(
    private val textRolePredictor: ComposeTextRolePredictor = DefaultComposeTextRolePredictor()
) {

    fun extractFeatures(components: List<UiComponent>): ComposeStyleFeatureSet {
        val composeComponents = ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType == SourceType.COMPOSE }

        return ComposeStyleFeatureSet(
            spacingValuesDp = composeComponents.flatMap { component -> collectSpacingValues(component) },
            textSizesSp = composeComponents.mapNotNull { component ->
                DimensionUtils.parseSp(component.properties.textSize)
            },
            colorValues = composeComponents.flatMap { component -> collectColorValues(component) },
            textStyleSignatures = composeComponents
                .filter { component -> component.type == ComponentTypes.COMPOSE_TEXT }
                .map { component -> extractTextStyleSignature(component) },
            buttonStyleSignatures = composeComponents
                .filter { component -> component.type in composeButtonTypes }
                .map { component -> extractButtonStyleSignature(component) }
        )
    }

    private fun collectSpacingValues(component: UiComponent): List<Float> {
        return listOfNotNull(
            DimensionUtils.parseDp(component.properties.padding),
            DimensionUtils.parseDp(component.properties.width),
            DimensionUtils.parseDp(component.properties.height)
        )
    }

    private fun collectColorValues(component: UiComponent): List<String> {
        return listOfNotNull(
            ComposeColorValueNormalizer.normalize(component.properties.backgroundColor),
            ComposeColorValueNormalizer.normalize(component.properties.backgroundTint),
            ComposeColorValueNormalizer.normalize(component.properties.tint),
            ComposeColorValueNormalizer.normalize(component.properties.textColor)
        )
    }

    private fun extractTextStyleSignature(component: UiComponent): ComposeTextStyleSignature {
        return ComposeTextStyleSignature(
            role = textRolePredictor.predict(component),
            typographyStyle = component.properties.typographyStyle?.trim(),
            textSize = DimensionUtils.parseSp(component.properties.textSize),
            textStyle = component.properties.textStyle?.trim(),
            fontFamily = component.properties.fontFamily?.trim()
        )
    }

    private fun extractButtonStyleSignature(component: UiComponent): ComposeButtonStyleSignature {
        return ComposeButtonStyleSignature(
            type = component.type,
            containerColor = ComposeColorValueNormalizer.normalize(component.properties.backgroundColor),
            contentColor = ComposeColorValueNormalizer.normalize(component.properties.textColor),
            width = DimensionUtils.parseDp(component.properties.width),
            height = DimensionUtils.parseDp(component.properties.height),
            padding = DimensionUtils.parseDp(component.properties.padding)
        )
    }

    private companion object {
        val composeButtonTypes = setOf(
            ComponentTypes.COMPOSE_BUTTON,
            ComponentTypes.COMPOSE_ICON_BUTTON,
            ComponentTypes.COMPOSE_OUTLINED_BUTTON,
            ComponentTypes.COMPOSE_TEXT_BUTTON,
            ComponentTypes.COMPOSE_FLOATING_ACTION_BUTTON
        )
    }
}
