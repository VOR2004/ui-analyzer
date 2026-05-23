package ru.itis.compose.rules.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.model.SourceType
import ru.itis.model.UiBounds
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class RuntimeTextTruncationRiskRuleTest {
    private val rule = RuntimeTextTruncationRiskRule()

    @Test
    fun `reports long runtime text that does not fit actual bounds`() {
        val component = runtimeText(
            text = "Very long settings title",
            bounds = UiBounds(x = 0f, y = 0f, width = 40f, height = 20f)
        )

        val issues = rule.check(listOf(component))

        assertEquals(1, issues.size)
        assertEquals(RuleIds.RUNTIME_TEXT_TRUNCATION_RISK, issues.single().ruleId)
    }

    @Test
    fun `ignores short text with enough actual bounds`() {
        val component = runtimeText(
            text = "Settings",
            bounds = UiBounds(x = 0f, y = 0f, width = 120f, height = 20f)
        )

        val issues = rule.check(listOf(component))

        assertTrue(issues.isEmpty())
    }

    private fun runtimeText(text: String, bounds: UiBounds): UiComponent {
        return UiComponent(
            id = "title",
            type = "TextView",
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = "runtime.xml",
            treePath = "/TextView[0]",
            properties = UiProperties(
                text = text,
                bounds = bounds
            )
        )
    }
}
