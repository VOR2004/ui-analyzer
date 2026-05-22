package ru.itis.compose.rules.style
import ru.itis.analyzer.messages.rules.RuleIds

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeComponentStyleOutlierRuleTest {

    @Test
    fun `reports compose button with multiple style differences from dominant local style`() {
        val components = listOf(
            composeButton(
                id = "primary1",
                containerColor = "Color(0xFF1565C0)",
                width = "120.dp",
                height = "48.dp",
                padding = "16.dp"
            ),
            composeButton(
                id = "primary2",
                containerColor = "Color(0xFF1565C0)",
                width = "120.dp",
                height = "48.dp",
                padding = "16.dp"
            ),
            composeButton(
                id = "danger",
                containerColor = "Color(0xFFC62828)",
                width = "96.dp",
                height = "48.dp",
                padding = "12.dp"
            )
        )

        val issues = ComposeComponentStyleOutlierRule().check(components)

        assertEquals(1, issues.size)
        assertEquals(RuleIds.COMPOSE_COMPONENT_STYLE_OUTLIER, issues.single().ruleId)
        assertEquals("danger", issues.single().componentId)
        assertEquals(Severity.WARNING, issues.single().severity)
        val message = issues.single().message.orEmpty()
        assertTrue(message.contains("containerColor"))
        assertTrue(message.contains("width"))
        assertTrue(message.contains("padding"))
    }

    @Test
    fun `does not report single color difference handled by color consistency rule`() {
        val components = listOf(
            composeButton("primary1", "Color(0xFF1565C0)", "120.dp", "48.dp", "16.dp"),
            composeButton("primary2", "Color(0xFF1565C0)", "120.dp", "48.dp", "16.dp"),
            composeButton("danger", "Color(0xFFC62828)", "120.dp", "48.dp", "16.dp")
        )

        val issues = ComposeComponentStyleOutlierRule().check(components)

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `does not compare different compose button types`() {
        val components = listOf(
            composeButton("primary1", "Color(0xFF1565C0)", "120.dp", "48.dp", "16.dp"),
            composeButton("primary2", "Color(0xFF1565C0)", "120.dp", "48.dp", "16.dp"),
            composeButton(
                id = "fab",
                containerColor = "Color(0xFFC62828)",
                width = "56.dp",
                height = "56.dp",
                padding = "8.dp",
                type = ComponentTypes.COMPOSE_FLOATING_ACTION_BUTTON
            )
        )

        val issues = ComposeComponentStyleOutlierRule().check(components)

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `does not report xml buttons`() {
        val components = listOf(
            UiComponent(
                id = "xmlButton",
                type = ComponentTypes.BUTTON,
                sourceType = SourceType.XML,
                filePath = "demo.xml",
                properties = UiProperties(
                    backgroundColor = "#C62828",
                    width = "96dp",
                    height = "48dp",
                    padding = "12dp"
                )
            )
        )

        val issues = ComposeComponentStyleOutlierRule().check(components)

        assertTrue(issues.isEmpty())
    }

    private fun composeButton(
        id: String,
        containerColor: String,
        width: String,
        height: String,
        padding: String,
        type: String = ComponentTypes.COMPOSE_BUTTON
    ): UiComponent {
        return UiComponent(
            id = id,
            type = type,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            treePath = "/$type[$id]",
            properties = UiProperties(
                backgroundColor = containerColor,
                width = width,
                height = height,
                padding = padding
            )
        )
    }
}


