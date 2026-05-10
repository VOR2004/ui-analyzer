package ru.itis.compose.rules.text

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeTextContrastRuleTest {

    @Test
    fun `reports compose text with insufficient contrast against inherited parent background`() {
        val components = listOf(
            composeBox(
                backgroundColor = "Color(0xFFFFFFFF)",
                children = listOf(
                    composeText(
                        id = "mutedText",
                        textColor = "Color(0xFFCCCCCC)",
                        textSize = "16.sp"
                    )
                )
            )
        )

        val issues = ComposeTextContrastRule().check(components)

        assertEquals(1, issues.size)
        assertEquals(AnalyzerStrings.RuleIds.COMPOSE_TEXT_CONTRAST, issues.single().ruleId)
        assertEquals("mutedText", issues.single().componentId)
        assertEquals(Severity.WARNING, issues.single().severity)
        assertTrue(issues.single().message.orEmpty().contains("#FFCCCCCC"))
        assertTrue(issues.single().message.orEmpty().contains("#FFFFFFFF"))
    }

    @Test
    fun `does not report compose text with sufficient contrast`() {
        val components = listOf(
            composeBox(
                backgroundColor = "#FFFFFF",
                children = listOf(
                    composeText(
                        id = "title",
                        textColor = "#111111",
                        textSize = "16.sp"
                    )
                )
            )
        )

        val issues = ComposeTextContrastRule().check(components)

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `uses large text contrast threshold`() {
        val components = listOf(
            composeBox(
                backgroundColor = "#FFFFFF",
                children = listOf(
                    composeText(
                        id = "largeText",
                        textColor = "#888888",
                        textSize = "20.sp"
                    )
                )
            )
        )

        val issues = ComposeTextContrastRule().check(components)

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `skips theme colors because they are not resolved yet`() {
        val components = listOf(
            composeBox(
                backgroundColor = "MaterialTheme.colorScheme.background",
                children = listOf(
                    composeText(
                        id = "themeText",
                        textColor = "MaterialTheme.colorScheme.onBackground",
                        textSize = "16.sp"
                    )
                )
            )
        )

        val issues = ComposeTextContrastRule().check(components)

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `does not report xml text`() {
        val components = listOf(
            UiComponent(
                id = "xmlText",
                type = ComponentTypes.TEXT_VIEW,
                sourceType = SourceType.XML,
                filePath = "demo.xml",
                properties = UiProperties(
                    backgroundColor = "#FFFFFF",
                    textColor = "#CCCCCC",
                    textSize = "16sp"
                )
            )
        )

        val issues = ComposeTextContrastRule().check(components)

        assertTrue(issues.isEmpty())
    }

    private fun composeBox(
        backgroundColor: String,
        children: List<UiComponent>
    ): UiComponent {
        return UiComponent(
            id = null,
            type = ComponentTypes.COMPOSE_BOX,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            treePath = "/Box[0]",
            properties = UiProperties(backgroundColor = backgroundColor),
            children = children
        )
    }

    private fun composeText(
        id: String,
        textColor: String,
        textSize: String
    ): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.COMPOSE_TEXT,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            treePath = "/Box[0]/Text[$id]",
            properties = UiProperties(
                textColor = textColor,
                textSize = textSize
            )
        )
    }
}
