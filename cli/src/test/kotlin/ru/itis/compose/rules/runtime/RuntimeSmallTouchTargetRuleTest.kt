package ru.itis.compose.rules.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.model.SourceType
import ru.itis.model.UiBounds
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class RuntimeSmallTouchTargetRuleTest {
    private val rule = RuntimeSmallTouchTargetRule()

    @Test
    fun `reports clickable runtime component with small actual bounds`() {
        val component = runtimeComponent(
            bounds = UiBounds(x = 0f, y = 0f, width = 32f, height = 48f),
            isClickable = true
        )

        val issues = rule.check(listOf(component))

        assertEquals(1, issues.size)
        assertEquals(RuleIds.RUNTIME_SMALL_TOUCH_TARGET, issues.single().ruleId)
    }

    @Test
    fun `ignores non-clickable and sufficiently large runtime components`() {
        val nonClickable = runtimeComponent(
            bounds = UiBounds(x = 0f, y = 0f, width = 20f, height = 20f),
            isClickable = false
        )
        val largeClickable = runtimeComponent(
            bounds = UiBounds(x = 0f, y = 0f, width = 64f, height = 64f),
            isClickable = true
        )

        val issues = rule.check(listOf(nonClickable, largeClickable))

        assertTrue(issues.isEmpty())
    }

    private fun runtimeComponent(bounds: UiBounds, isClickable: Boolean): UiComponent {
        return UiComponent(
            id = "action",
            type = "Button",
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = "runtime.xml",
            treePath = "/Button[0]",
            properties = UiProperties(
                isClickable = isClickable,
                bounds = bounds
            )
        )
    }
}
