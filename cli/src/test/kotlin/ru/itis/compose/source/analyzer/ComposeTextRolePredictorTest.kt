package ru.itis.compose.source.analyzer

import kotlin.test.Test
import kotlin.test.assertEquals
import ru.itis.analyzer.config.ComponentTypes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties
import ru.itis.compose.style.signature.ComposePredictedTextRole

class ComposeTextRolePredictorTest {

    private val predictor = ComposeTextRolePredictor()

    @Test
    fun `predicts title by material typography style`() {
        val component = composeText(
            UiProperties(typographyStyle = "MaterialTheme.typography.titleLarge")
        )

        assertEquals(ComposePredictedTextRole.TITLE, predictor.predict(component))
    }

    @Test
    fun `predicts caption by small text size`() {
        val component = composeText(
            UiProperties(textSize = "12.sp")
        )

        assertEquals(ComposePredictedTextRole.CAPTION, predictor.predict(component))
    }

    @Test
    fun `predicts title by large bold text`() {
        val component = composeText(
            UiProperties(textSize = "22.sp", textStyle = "FontWeight.Bold")
        )

        assertEquals(ComposePredictedTextRole.TITLE, predictor.predict(component))
    }

    @Test
    fun `predicts body by default`() {
        val component = composeText(
            UiProperties(textSize = "16.sp")
        )

        assertEquals(ComposePredictedTextRole.BODY, predictor.predict(component))
    }

    private fun composeText(properties: UiProperties): UiComponent {
        return UiComponent(
            id = null,
            type = ComponentTypes.COMPOSE_TEXT,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            properties = properties
        )
    }
}
