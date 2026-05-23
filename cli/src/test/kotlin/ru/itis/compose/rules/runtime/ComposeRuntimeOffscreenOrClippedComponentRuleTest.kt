package ru.itis.compose.rules.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiBounds
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeRuntimeOffscreenOrClippedComponentRuleTest {

    private val rule = ComposeRuntimeOffscreenOrClippedComponentRule()

    @Test
    fun `reports runtime component outside screen bounds`() {
        val root = runtimeRoot(
            child = runtimeComponent(
                id = "offscreen",
                bounds = UiBounds(x = 980f, y = 100f, width = 120f, height = 48f)
            )
        )

        val issues = rule.check(listOf(root))

        assertEquals(1, issues.size)
        assertEquals(rule.id, issues.single().ruleId)
        assertEquals("offscreen", issues.single().componentId)
        assertEquals(Severity.WARNING, issues.single().severity)
        assertTrue(issues.single().message?.contains("outside screen bounds") == true)
    }

    @Test
    fun `reports runtime component with almost zero size`() {
        val root = runtimeRoot(
            child = runtimeComponent(
                id = "thin",
                bounds = UiBounds(x = 20f, y = 20f, width = 1f, height = 48f)
            )
        )

        val issues = rule.check(listOf(root))

        assertEquals(1, issues.size)
        assertEquals("thin", issues.single().componentId)
        assertEquals(Severity.INFO, issues.single().severity)
        assertTrue(issues.single().message?.contains("almost zero visible size") == true)
    }

    @Test
    fun `ignores runtime component inside screen bounds`() {
        val root = runtimeRoot(
            child = runtimeComponent(
                id = "visible",
                bounds = UiBounds(x = 20f, y = 20f, width = 100f, height = 48f)
            )
        )

        val issues = rule.check(listOf(root))

        assertTrue(issues.isEmpty())
    }

    private fun runtimeRoot(child: UiComponent): UiComponent {
        return UiComponent(
            id = "root",
            type = "Root",
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = "runtime.xml",
            treePath = "/Root[0]",
            properties = UiProperties(
                bounds = UiBounds(x = 0f, y = 0f, width = 1080f, height = 1920f)
            ),
            children = listOf(child)
        )
    }

    private fun runtimeComponent(id: String, bounds: UiBounds): UiComponent {
        return UiComponent(
            id = id,
            type = "View",
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = "runtime.xml",
            treePath = "/Root[0]/View[$id]",
            properties = UiProperties(bounds = bounds)
        )
    }
}
