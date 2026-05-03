package ru.itis.compose.rules.color

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeHardcodedColorRuleTest {

    @Test
    fun `reports compose hardcoded color constructor`() {
        val component = composeComponent(
            properties = UiProperties(
                textColor = "Color(0xFF000000)"
            )
        )

        val issues = ComposeHardcodedColorRule().check(listOf(component))

        assertTrue(
            issues.any { issue -> issue.ruleId == AnalyzerStrings.RuleIds.COMPOSE_HARDCODED_COLOR },
            "Expected Compose hardcoded color issue"
        )
    }

    @Test
    fun `does not report named compose color constants`() {
        val component = composeComponent(
            properties = UiProperties(
                backgroundColor = "Color.White",
                textColor = "Color.Black"
            )
        )

        val issues = ComposeHardcodedColorRule().check(listOf(component))

        assertFalse(issues.any())
    }

    @Test
    fun `reports compose hex color literals`() {
        val component = composeComponent(
            properties = UiProperties(textColor = "#FFFFFF")
        )

        val issues = ComposeHardcodedColorRule().check(listOf(component))

        assertTrue(
            issues.any { issue -> issue.ruleId == AnalyzerStrings.RuleIds.COMPOSE_HARDCODED_COLOR },
            "Expected Compose hex color literal issue"
        )
    }

    @Test
    fun `does not report material theme colors`() {
        val component = composeComponent(
            properties = UiProperties(textColor = "MaterialTheme.colorScheme.primary")
        )

        val issues = ComposeHardcodedColorRule().check(listOf(component))

        assertFalse(issues.any())
    }

    @Test
    fun `does not report xml colors`() {
        val component = UiComponent(
            id = null,
            type = ComponentTypes.TEXT_VIEW,
            sourceType = SourceType.XML,
            filePath = "demo",
            properties = UiProperties(textColor = "#FFFFFF")
        )

        val issues = ComposeHardcodedColorRule().check(listOf(component))

        assertFalse(issues.any())
    }

    private fun composeComponent(properties: UiProperties): UiComponent {
        return UiComponent(
            id = null,
            type = ComponentTypes.COMPOSE_TEXT,
            sourceType = SourceType.COMPOSE,
            filePath = "demo",
            properties = properties
        )
    }
}
