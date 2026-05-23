package ru.itis.compose.rules.layout
import ru.itis.analyzer.messages.rules.RuleIds

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeAdaptiveSpacingOutlierRuleTest {

    @Test
    fun `reports compose spacing value outside local scale`() {
        val components = listOf(
            composeComponent("text1", UiProperties(padding = "8.dp")),
            composeComponent("text2", UiProperties(padding = "8.dp")),
            composeComponent("text3", UiProperties(padding = "16.dp")),
            composeComponent("text4", UiProperties(padding = "16.dp")),
            composeComponent("text5", UiProperties(padding = "30.dp"))
        )

        val issues = ComposeAdaptiveSpacingOutlierRule().check(components)

        assertEquals(1, issues.size)
        assertEquals(RuleIds.COMPOSE_ADAPTIVE_SPACING_OUTLIER, issues.single().ruleId)
        assertEquals("text5", issues.single().componentId)
    }

    @Test
    fun `does not report values near local compose scale`() {
        val components = listOf(
            composeComponent("text1", UiProperties(padding = "8.dp")),
            composeComponent("text2", UiProperties(padding = "8.dp")),
            composeComponent("text3", UiProperties(padding = "16.dp")),
            composeComponent("text4", UiProperties(padding = "16.dp")),
            composeComponent("text5", UiProperties(padding = "18.dp"))
        )

        val issues = ComposeAdaptiveSpacingOutlierRule().check(components)

        assertFalse(issues.any())
    }

    @Test
    fun `does not report xml spacing values`() {
        val components = listOf(
            xmlComponent("text1", UiProperties(padding = "8dp")),
            xmlComponent("text2", UiProperties(padding = "8dp")),
            xmlComponent("text3", UiProperties(padding = "16dp")),
            xmlComponent("text4", UiProperties(padding = "16dp")),
            xmlComponent("text5", UiProperties(padding = "30dp"))
        )

        val issues = ComposeAdaptiveSpacingOutlierRule().check(components)

        assertTrue(issues.isEmpty())
    }

    private fun composeComponent(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.COMPOSE_TEXT,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            treePath = "/Text[$id]",
            properties = properties
        )
    }

    private fun xmlComponent(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.TEXT_VIEW,
            sourceType = SourceType.XML,
            filePath = "demo.xml",
            properties = properties
        )
    }
}


