package ru.itis.compose.style.extractor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.compose.style.signature.ComposePredictedTextRole
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeStyleFeatureExtractorTest {

    @Test
    fun `extracts compose visual style features`() {
        val components = listOf(
            composeComponent(
                id = "title",
                type = ComponentTypes.COMPOSE_TEXT,
                properties = UiProperties(
                    padding = "16.dp",
                    textSize = "22.sp",
                    textStyle = "FontWeight.Bold",
                    textColor = "Color(0xFF000000)"
                )
            ),
            composeComponent(
                id = "button",
                type = ComponentTypes.COMPOSE_BUTTON,
                properties = UiProperties(
                    width = "120.dp",
                    height = "48.dp",
                    backgroundColor = "Color(0xFF1565C0)",
                    textColor = "Color(0xFFFFFFFF)"
                )
            )
        )

        val features = ComposeStyleFeatureExtractor().extractFeatures(components)

        assertTrue(16f in features.spacingValuesDp)
        assertTrue(120f in features.spacingValuesDp)
        assertTrue(22f in features.textSizesSp)
        assertTrue("#FF1565C0" in features.colorValues)
        assertEquals(ComposePredictedTextRole.TITLE, features.textStyleSignatures.single().role)
        assertEquals("#FF1565C0", features.buttonStyleSignatures.single().containerColor)
    }

    private fun composeComponent(
        id: String,
        type: String,
        properties: UiProperties
    ): UiComponent {
        return UiComponent(
            id = id,
            type = type,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            treePath = "/$type[$id]",
            properties = properties
        )
    }
}
