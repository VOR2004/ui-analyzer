package ru.itis.compose.rules.accessibility
import ru.itis.analyzer.messages.rules.RuleIds

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeTouchTargetTooSmallRuleTest {

    @Test
    fun `reports small compose interactive component`() {
        val component = composeButton(
            id = "smallButton",
            properties = UiProperties(width = "40.dp", height = "48.dp")
        )

        val issues = ComposeTouchTargetTooSmallRule().check(listOf(component))

        assertEquals(1, issues.size)
        assertEquals(RuleIds.COMPOSE_TOUCH_TARGET_TOO_SMALL, issues.single().ruleId)
        assertEquals("smallButton", issues.single().componentId)
    }

    @Test
    fun `does not report compose interactive component with enough size`() {
        val component = composeButton(
            id = "normalButton",
            properties = UiProperties(width = "48.dp", height = "48.dp")
        )

        val issues = ComposeTouchTargetTooSmallRule().check(listOf(component))

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `does not report compose interactive component without explicit size`() {
        val component = composeButton(
            id = "unknownSizeButton",
            properties = UiProperties()
        )

        val issues = ComposeTouchTargetTooSmallRule().check(listOf(component))

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `does not report xml button`() {
        val component = UiComponent(
            id = "smallXmlButton",
            type = ComponentTypes.BUTTON,
            sourceType = SourceType.XML,
            filePath = "demo.xml",
            properties = UiProperties(width = "40dp", height = "48dp")
        )

        val issues = ComposeTouchTargetTooSmallRule().check(listOf(component))

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `reports small clickable compose container`() {
        val component = UiComponent(
            id = "clickableBox",
            type = ComponentTypes.COMPOSE_BOX,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            treePath = "/Box[0]",
            properties = UiProperties(
                width = "40.dp",
                height = "40.dp",
                isClickable = true
            )
        )

        val issues = ComposeTouchTargetTooSmallRule().check(listOf(component))

        assertEquals(1, issues.size)
        assertEquals("clickableBox", issues.single().componentId)
    }

    private fun composeButton(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.COMPOSE_ICON_BUTTON,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            treePath = "/IconButton[$id]",
            properties = properties
        )
    }
}


