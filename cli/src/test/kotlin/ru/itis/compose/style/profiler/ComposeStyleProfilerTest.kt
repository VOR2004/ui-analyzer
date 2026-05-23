package ru.itis.compose.style.profiler

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.compose.style.signature.ComposePredictedTextRole
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeStyleProfilerTest {

    @Test
    fun `builds compose project style profile`() {
        val components = listOf(
            composeText("body1", UiProperties(textSize = "16.sp")),
            composeText("body2", UiProperties(textSize = "16.sp")),
            composeText("title", UiProperties(textSize = "22.sp", textStyle = "FontWeight.Bold")),
            composeButton("button1", UiProperties(width = "120.dp", height = "48.dp", backgroundColor = "Color(0xFF1565C0)")),
            composeButton("button2", UiProperties(width = "120.dp", height = "48.dp", backgroundColor = "Color(0xFF1565C0)"))
        )

        val profile = ComposeStyleProfiler().buildProjectProfile(components)

        assertTrue(profile.spacingClusters.isNotEmpty())
        assertTrue(profile.textSizeClusters.isNotEmpty())
        assertNotNull(profile.dominantButtonStyle)
        assertEquals("#FF1565C0", profile.colorPalette.first())
        assertEquals("16.sp", profile.dominantTextStylesByRole.getValue(ComposePredictedTextRole.BODY).textSize?.let { "${it.toInt()}.sp" })
    }

    @Test
    fun `builds compose screen profiles by file path`() {
        val components = listOf(
            composeText("first", UiProperties(textSize = "16.sp"), filePath = "First.kt"),
            composeText("second", UiProperties(textSize = "18.sp"), filePath = "Second.kt")
        )

        val profiles = ComposeStyleProfiler().buildScreenProfiles(components)

        assertEquals(setOf("First.kt", "Second.kt"), profiles.keys)
    }

    private fun composeText(
        id: String,
        properties: UiProperties,
        filePath: String = "Demo.kt"
    ): UiComponent {
        return composeComponent(
            id = id,
            type = ComponentTypes.COMPOSE_TEXT,
            properties = properties,
            filePath = filePath
        )
    }

    private fun composeButton(
        id: String,
        properties: UiProperties
    ): UiComponent {
        return composeComponent(
            id = id,
            type = ComponentTypes.COMPOSE_BUTTON,
            properties = properties,
            filePath = "Demo.kt"
        )
    }

    private fun composeComponent(
        id: String,
        type: String,
        properties: UiProperties,
        filePath: String
    ): UiComponent {
        return UiComponent(
            id = id,
            type = type,
            sourceType = SourceType.COMPOSE,
            filePath = filePath,
            treePath = "/$type[$id]",
            properties = properties
        )
    }
}
