package ru.itis.compose.rules.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class RuntimeDuplicateVisibleTextActionsRuleTest {

    private val rule = RuntimeDuplicateVisibleTextActionsRule()

    @Test
    fun `reports duplicate labels from clickable runtime actions`() {
        val root = runtimeRoot(
            children = listOf(
                clickableContainer(id = "first", text = "Demo board"),
                clickableContainer(id = "second", text = "Demo board")
            )
        )

        val issues = rule.check(listOf(root))

        assertEquals(1, issues.size)
        assertEquals(rule.id, issues.single().ruleId)
        assertEquals(Severity.INFO, issues.single().severity)
        assertTrue(issues.single().message?.contains("Demo board") == true)
        assertTrue(issues.single().message?.contains("2") == true)
    }

    @Test
    fun `ignores unique runtime action labels`() {
        val root = runtimeRoot(
            children = listOf(
                clickableContainer(id = "first", text = "Demo board"),
                clickableContainer(id = "second", text = "Settings")
            )
        )

        val issues = rule.check(listOf(root))

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `uses content description as action label`() {
        val root = runtimeRoot(
            children = listOf(
                clickableContainer(id = "first", contentDescription = "Delete board"),
                clickableContainer(id = "second", contentDescription = "Delete board")
            )
        )

        val issues = rule.check(listOf(root))

        assertEquals(1, issues.size)
        assertTrue(issues.single().message?.contains("Delete board") == true)
    }

    private fun runtimeRoot(children: List<UiComponent>): UiComponent {
        return UiComponent(
            id = "root",
            type = "Root",
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = "runtime.xml",
            treePath = "/Root[0]",
            properties = UiProperties(),
            children = children
        )
    }

    private fun clickableContainer(
        id: String,
        text: String? = null,
        contentDescription: String? = null
    ): UiComponent {
        return UiComponent(
            id = id,
            type = "FrameLayout",
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = "runtime.xml",
            treePath = "/Root[0]/FrameLayout[$id]",
            properties = UiProperties(
                contentDescription = contentDescription,
                isClickable = true
            ),
            children = listOfNotNull(
                text?.let { value ->
                    UiComponent(
                        id = "$id-label",
                        type = "TextView",
                        sourceType = SourceType.ANDROID_RUNTIME,
                        filePath = "runtime.xml",
                        treePath = "/Root[0]/FrameLayout[$id]/TextView[0]",
                        properties = UiProperties(text = value)
                    )
                }
            )
        )
    }
}
