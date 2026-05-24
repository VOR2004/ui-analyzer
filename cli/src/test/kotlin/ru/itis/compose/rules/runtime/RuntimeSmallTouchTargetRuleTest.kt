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

    @Test
    fun `uses estimated screen density for android runtime bounds`() {
        val root = runtimeRoot(
            child = runtimeComponent(
                bounds = UiBounds(x = 40f, y = 120f, width = 88f, height = 88f),
                isClickable = true
            )
        )

        val issues = rule.check(listOf(root))

        assertEquals(1, issues.size)
        assertEquals(RuleIds.RUNTIME_SMALL_TOUCH_TARGET, issues.single().ruleId)
    }

    @Test
    fun `treats focusable runtime node as interactive touch target`() {
        val root = runtimeRoot(
            child = runtimeComponent(
                bounds = UiBounds(x = 40f, y = 120f, width = 88f, height = 88f),
                isClickable = false,
                rawAttributes = mapOf("focusable" to "true")
            )
        )

        val issues = rule.check(listOf(root))

        assertEquals(1, issues.size)
        assertEquals(RuleIds.RUNTIME_SMALL_TOUCH_TARGET, issues.single().ruleId)
    }

    private fun runtimeRoot(child: UiComponent): UiComponent {
        return UiComponent(
            id = "root",
            type = "FrameLayout",
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = "runtime.xml",
            treePath = "/FrameLayout[0]",
            properties = UiProperties(
                bounds = UiBounds(x = 0f, y = 0f, width = 1080f, height = 2400f)
            ),
            children = listOf(child)
        )
    }

    private fun runtimeComponent(
        bounds: UiBounds,
        isClickable: Boolean,
        rawAttributes: Map<String, String> = emptyMap()
    ): UiComponent {
        return UiComponent(
            id = "action",
            type = "Button",
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = "runtime.xml",
            treePath = "/Button[0]",
            properties = UiProperties(
                isClickable = isClickable,
                bounds = bounds,
                rawAttributes = rawAttributes
            )
        )
    }
}
