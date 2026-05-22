package ru.itis.compose.rules.api
import ru.itis.analyzer.messages.rules.RuleIds

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.itis.analyzer.core.ComposeSourceAnalysisContext
import ru.itis.compose.source.model.ComposeFunction

class ComposeMissingModifierParameterRuleTest {

    @Test
    fun `reports reusable composable without modifier parameter`() {
        val function = ComposeFunction(
            name = "UserCard",
            parameters = listOf("user: User"),
            filePath = "demo"
        )

        val issues = ComposeMissingModifierParameterRule()
            .check(ComposeSourceAnalysisContext(listOf(function)))

        assertTrue(
            issues.any { issue ->
                issue.ruleId == RuleIds.COMPOSE_MISSING_MODIFIER_PARAMETER
            }
        )
    }

    @Test
    fun `does not report composable with modifier parameter`() {
        val function = ComposeFunction(
            name = "UserCard",
            parameters = listOf("user: User", "modifier: Modifier = Modifier"),
            filePath = "demo"
        )

        val issues = ComposeMissingModifierParameterRule()
            .check(ComposeSourceAnalysisContext(listOf(function)))

        assertFalse(issues.any())
    }

    @Test
    fun `does not report lowercase helper composable`() {
        val function = ComposeFunction(
            name = "debugLabel",
            parameters = emptyList(),
            filePath = "demo"
        )

        val issues = ComposeMissingModifierParameterRule()
            .check(ComposeSourceAnalysisContext(listOf(function)))

        assertFalse(issues.any())
    }

    @Test
    fun `does not report private composable without modifier`() {
        val function = ComposeFunction(
            name = "CollapsedBottomBar",
            parameters = listOf("state: BoardScreenState"),
            filePath = "demo",
            modifiers = setOf("private")
        )

        val issues = ComposeMissingModifierParameterRule()
            .check(ComposeSourceAnalysisContext(listOf(function)))

        assertFalse(issues.any())
    }

    @Test
    fun `does not report theme wrapper without modifier`() {
        val function = ComposeFunction(
            name = "UniboardTheme",
            parameters = listOf("content: @Composable () -> Unit"),
            filePath = "demo"
        )

        val issues = ComposeMissingModifierParameterRule()
            .check(ComposeSourceAnalysisContext(listOf(function)))

        assertFalse(issues.any())
    }

    @Test
    fun `does not report unknown uppercase composable`() {
        val function = ComposeFunction(
            name = "Something",
            parameters = emptyList(),
            filePath = "demo"
        )

        val issues = ComposeMissingModifierParameterRule()
            .check(ComposeSourceAnalysisContext(listOf(function)))

        assertFalse(issues.any())
    }
}


