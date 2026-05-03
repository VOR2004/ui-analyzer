package ru.itis.xml.style.extractor

import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.UiComponent
import ru.itis.xml.style.signature.TextRolePredictor
import ru.itis.xml.style.signature.TextStyleSignature

class TextStyleSignatureExtractor(
    private val resourceRepository: ResourceRepository = ResourceRepository.empty(),
    private val textRolePredictor: TextRolePredictor = TextRolePredictor()
) {

    fun extract(component: UiComponent): TextStyleSignature? {
        val textSize = DimensionUtils.parseSp(resolveDimension(component.properties.textSize))
        val text = resourceRepository.resolveString(component.properties.text)
            ?: component.properties.text

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

    private fun resolveDimension(value: String?): String? {
        return resourceRepository.resolveDimension(value) ?: value
    }
}
