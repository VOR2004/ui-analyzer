package ru.itis.compose.rules.accessibility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeImageContentDescriptionRuleTest {

    @Test
    fun `reports missing compose image content description`() {
        val image = component(
            type = ComponentTypes.COMPOSE_IMAGE,
            properties = UiProperties(contentDescription = null)
        )

        val issues = ComposeImageContentDescriptionRule().check(listOf(image))

        assertTrue(issues.any { it.ruleId == AnalyzerStrings.RuleIds.COMPOSE_IMAGE_CONTENT_DESCRIPTION })
        assertEquals(Severity.INFO, issues.first().severity)
    }

    @Test
    fun `reports warning for icon inside interactive compose container`() {
        val button = component(
            type = ComponentTypes.COMPOSE_ICON_BUTTON,
            children = listOf(
                component(
                    type = ComponentTypes.COMPOSE_ICON,
                    properties = UiProperties(contentDescription = "null")
                )
            )
        )

        val issues = ComposeImageContentDescriptionRule().check(listOf(button))

        assertTrue(issues.any { it.componentType == ComponentTypes.COMPOSE_ICON })
        assertEquals(Severity.WARNING, issues.first().severity)
    }

    @Test
    fun `does not report described compose image`() {
        val image = component(
            type = ComponentTypes.COMPOSE_IMAGE,
            properties = UiProperties(contentDescription = "Avatar")
        )

        val issues = ComposeImageContentDescriptionRule().check(listOf(image))

        assertFalse(issues.any())
    }

    private fun component(
        type: String,
        properties: UiProperties = UiProperties(),
        children: List<UiComponent> = emptyList()
    ): UiComponent {
        return UiComponent(
            id = null,
            type = type,
            sourceType = SourceType.COMPOSE,
            filePath = "demo",
            properties = properties,
            children = children
        )
    }
}
