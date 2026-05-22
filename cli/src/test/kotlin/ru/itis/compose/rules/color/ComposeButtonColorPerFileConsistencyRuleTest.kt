package ru.itis.compose.rules.color

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeButtonColorPerFileConsistencyRuleTest {

    @Test
    fun `reports compose button color different from dominant file color`() {
        val components = listOf(
            composeButton("primary1", "Color(0xFF1565C0)"),
            composeButton("primary2", "Color(0xFF1565C0)"),
            composeButton("danger", "Color(0xFFC62828)")
        )

        val issues = ComposeButtonColorPerFileConsistencyRule().check(components)

        assertEquals(1, issues.size)
        assertEquals(AnalyzerStrings.RuleIds.COMPOSE_BUTTON_COLOR_PER_FILE_CONSISTENCY, issues.single().ruleId)
        assertEquals("danger", issues.single().componentId)
        assertEquals(Severity.WARNING, issues.single().severity)
    }

    @Test
    fun `reports near duplicate compose button color cluster`() {
        val components = listOf(
            composeButton("primary1", "Color(0xFF1565C0)"),
            composeButton("primary2", "Color(0xFF1565C0)"),
            composeButton("almostPrimary", "Color(0xFF1566C1)")
        )

        val issues = ComposeButtonColorPerFileConsistencyRule().check(components)

        assertEquals(1, issues.size)
        assertEquals(
            AnalyzerStrings.RuleIds.nearDuplicateCluster(
                AnalyzerStrings.RuleIds.COMPOSE_BUTTON_COLOR_PER_FILE_CONSISTENCY
            ),
            issues.single().ruleId
        )
        assertEquals("almostPrimary", issues.single().componentId)
        assertEquals(Severity.INFO, issues.single().severity)
    }

    @Test
    fun `does not report compose buttons with same color`() {
        val components = listOf(
            composeButton("primary1", "Color(0xFF1565C0)"),
            composeButton("primary2", "Color(0xFF1565C0)")
        )

        val issues = ComposeButtonColorPerFileConsistencyRule().check(components)

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `does not report xml buttons`() {
        val components = listOf(
            xmlButton("primary1", "#1565C0"),
            xmlButton("primary2", "#1565C0"),
            xmlButton("danger", "#C62828")
        )

        val issues = ComposeButtonColorPerFileConsistencyRule().check(components)

        assertTrue(issues.isEmpty())
    }

    private fun composeButton(id: String, color: String): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.COMPOSE_BUTTON,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            treePath = "/Button[$id]",
            properties = UiProperties(backgroundColor = color)
        )
    }

    private fun xmlButton(id: String, color: String): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.BUTTON,
            sourceType = SourceType.XML,
            filePath = "demo.xml",
            properties = UiProperties(backgroundColor = color)
        )
    }
}
