package ru.itis.compose.rules.text
import ru.itis.analyzer.messages.rules.RuleIds

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeHardcodedTextRuleTest {

    @Test
    fun `reports compose text string literal`() {
        val component = composeText(text = "Hello")

        val issues = ComposeHardcodedTextRule().check(listOf(component))

        assertTrue(
            issues.any { issue ->
                issue.ruleId == RuleIds.COMPOSE_HARDCODED_TEXT &&
                    issue.componentType == ComponentTypes.COMPOSE_TEXT
            },
            "Expected Compose Text string literal to be reported"
        )
    }

    @Test
    fun `does not report compose text without extracted literal`() {
        val component = composeText(text = null)

        val issues = ComposeHardcodedTextRule().check(listOf(component))

        assertFalse(issues.any())
    }

    @Test
    fun `does not report xml text`() {
        val component = UiComponent(
            id = null,
            type = ComponentTypes.TEXT_VIEW,
            sourceType = SourceType.XML,
            filePath = "demo",
            properties = UiProperties(text = "Hello")
        )

        val issues = ComposeHardcodedTextRule().check(listOf(component))

        assertFalse(issues.any())
    }

    private fun composeText(text: String?): UiComponent {
        return UiComponent(
            id = null,
            type = ComponentTypes.COMPOSE_TEXT,
            sourceType = SourceType.COMPOSE,
            filePath = "demo",
            properties = UiProperties(text = text)
        )
    }
}


