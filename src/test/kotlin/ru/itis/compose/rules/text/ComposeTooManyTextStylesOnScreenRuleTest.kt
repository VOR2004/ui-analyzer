package ru.itis.compose.rules.text

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeTooManyTextStylesOnScreenRuleTest {

    @Test
    fun `reports too many competing compose text styles in one file`() {
        val components = listOf(
            composeText("title1", UiProperties(textSize = "24.sp", textStyle = "FontWeight.Bold")),
            composeText("title2", UiProperties(textSize = "24.sp", textStyle = "FontWeight.Bold")),
            composeText("body1", UiProperties(textSize = "16.sp")),
            composeText("body2", UiProperties(textSize = "16.sp")),
            composeText("caption1", UiProperties(textSize = "12.sp")),
            composeText("caption2", UiProperties(textSize = "12.sp"))
        )

        val issues = ComposeTooManyTextStylesOnScreenRule().check(components)

        assertEquals(1, issues.size)
        assertEquals(AnalyzerStrings.RuleIds.COMPOSE_TOO_MANY_TEXT_STYLES_ON_SCREEN, issues.single().ruleId)
    }

    @Test
    fun `does not report consistent compose text styles`() {
        val components = listOf(
            composeText("body1", UiProperties(textSize = "16.sp")),
            composeText("body2", UiProperties(textSize = "16.sp")),
            composeText("body3", UiProperties(textSize = "16.sp")),
            composeText("body4", UiProperties(textSize = "16.sp"))
        )

        val issues = ComposeTooManyTextStylesOnScreenRule().check(components)

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `reports too many material typography styles`() {
        val components = listOf(
            composeText("title1", UiProperties(typographyStyle = "MaterialTheme.typography.titleLarge")),
            composeText("title2", UiProperties(typographyStyle = "MaterialTheme.typography.titleLarge")),
            composeText("body1", UiProperties(typographyStyle = "MaterialTheme.typography.bodyMedium")),
            composeText("body2", UiProperties(typographyStyle = "MaterialTheme.typography.bodyMedium")),
            composeText("label1", UiProperties(typographyStyle = "MaterialTheme.typography.labelSmall")),
            composeText("label2", UiProperties(typographyStyle = "MaterialTheme.typography.labelSmall"))
        )

        val issues = ComposeTooManyTextStylesOnScreenRule().check(components)

        assertEquals(1, issues.size)
    }

    @Test
    fun `does not report xml text styles`() {
        val components = listOf(
            xmlText("title1", UiProperties(textSize = "24sp", textStyle = "bold")),
            xmlText("title2", UiProperties(textSize = "24sp", textStyle = "bold")),
            xmlText("body1", UiProperties(textSize = "16sp")),
            xmlText("body2", UiProperties(textSize = "16sp")),
            xmlText("caption1", UiProperties(textSize = "12sp")),
            xmlText("caption2", UiProperties(textSize = "12sp"))
        )

        val issues = ComposeTooManyTextStylesOnScreenRule().check(components)

        assertTrue(issues.isEmpty())
    }

    private fun composeText(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.COMPOSE_TEXT,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            treePath = "/Text[$id]",
            properties = properties
        )
    }

    private fun xmlText(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.TEXT_VIEW,
            sourceType = SourceType.XML,
            filePath = "demo.xml",
            properties = properties
        )
    }
}
