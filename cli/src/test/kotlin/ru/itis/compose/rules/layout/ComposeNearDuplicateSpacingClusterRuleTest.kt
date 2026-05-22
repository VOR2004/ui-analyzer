package ru.itis.compose.rules.layout
import ru.itis.analyzer.messages.rules.RuleIds

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeNearDuplicateSpacingClusterRuleTest {

    @Test
    fun `reports near duplicate compose spacing values`() {
        val components = listOf(
            composeBox("box1", UiProperties(padding = "16.dp")),
            composeBox("box2", UiProperties(padding = "16.dp")),
            composeBox("box3", UiProperties(padding = "15.dp"))
        )

        val issues = ComposeNearDuplicateSpacingClusterRule().check(components)

        assertEquals(1, issues.size)
        assertEquals(RuleIds.COMPOSE_NEAR_DUPLICATE_SPACING_CLUSTER, issues.single().ruleId)
        assertEquals("box3", issues.single().componentId)
        assertTrue(issues.single().recommendation.contains("16.0dp"))
    }

    @Test
    fun `does not report distant compose spacing values`() {
        val components = listOf(
            composeBox("box1", UiProperties(padding = "8.dp")),
            composeBox("box2", UiProperties(padding = "16.dp")),
            composeBox("box3", UiProperties(padding = "24.dp"))
        )

        val issues = ComposeNearDuplicateSpacingClusterRule().check(components)

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `does not report xml spacing values`() {
        val components = listOf(
            xmlBox("box1", UiProperties(padding = "16dp")),
            xmlBox("box2", UiProperties(padding = "16dp")),
            xmlBox("box3", UiProperties(padding = "15dp"))
        )

        val issues = ComposeNearDuplicateSpacingClusterRule().check(components)

        assertTrue(issues.isEmpty())
    }

    private fun composeBox(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.COMPOSE_BOX,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            treePath = "/Box[$id]",
            properties = properties
        )
    }

    private fun xmlBox(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.FRAME_LAYOUT,
            sourceType = SourceType.XML,
            filePath = "demo.xml",
            properties = properties
        )
    }
}


