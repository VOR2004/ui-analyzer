package ru.itis.compose.rules.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.model.SourceType
import ru.itis.model.UiBounds
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeRuntimeOverlappingClickableComponentsRuleTest {

    private val rule = ComposeRuntimeOverlappingClickableComponentsRule()

    @Test
    fun `reports overlapping runtime clickable components`() {
        val first = runtimeClickable(
            id = "primary",
            bounds = UiBounds(x = 0f, y = 0f, width = 100f, height = 60f)
        )
        val second = runtimeClickable(
            id = "secondary",
            bounds = UiBounds(x = 80f, y = 40f, width = 100f, height = 60f)
        )

        val issues = rule.check(listOf(first, second))

        assertEquals(1, issues.size)
        assertEquals(rule.id, issues.single().ruleId)
        assertEquals("secondary", issues.single().componentId)
        assertTrue(issues.single().message?.contains("overlapArea=400px2") == true)
    }

    @Test
    fun `ignores static compose components and non-overlapping runtime components`() {
        val staticCompose = runtimeClickable(
            id = "static",
            sourceType = SourceType.COMPOSE,
            bounds = UiBounds(x = 0f, y = 0f, width = 100f, height = 60f)
        )
        val runtime = runtimeClickable(
            id = "runtime",
            bounds = UiBounds(x = 200f, y = 200f, width = 100f, height = 60f)
        )

        val issues = rule.check(listOf(staticCompose, runtime))

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `checks android runtime components from uiautomator dump`() {
        val first = runtimeClickable(
            id = "first",
            sourceType = SourceType.ANDROID_RUNTIME,
            bounds = UiBounds(x = 0f, y = 0f, width = 100f, height = 60f)
        )
        val second = runtimeClickable(
            id = "second",
            sourceType = SourceType.ANDROID_RUNTIME,
            bounds = UiBounds(x = 90f, y = 40f, width = 100f, height = 60f)
        )

        val issues = rule.check(listOf(first, second))

        assertEquals(1, issues.size)
        assertEquals("second", issues.single().componentId)
    }

    private fun runtimeClickable(
        id: String,
        bounds: UiBounds,
        sourceType: SourceType = SourceType.COMPOSE_RUNTIME
    ): UiComponent {
        return UiComponent(
            id = id,
            type = "Button",
            sourceType = sourceType,
            filePath = "runtime.json",
            treePath = "/Button[$id]",
            properties = UiProperties(
                isClickable = true,
                bounds = bounds,
                rawAttributes = mapOf(
                    "runtime:screen" to "BoardScreen",
                    "runtime:state" to "content"
                )
            )
        )
    }
}
