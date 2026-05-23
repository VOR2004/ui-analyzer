package ru.itis.compose.rules.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class RuntimeSystemAppSnapshotWarningRuleTest {

    @Test
    fun `reports runtime snapshot from unexpected package`() {
        val rule = RuntimeSystemAppSnapshotWarningRule(expectedPackageName = "com.uniboard")
        val root = runtimeNode(
            packageName = "com.google.android.apps.nexuslauncher",
            child = runtimeNode(packageName = "com.google.android.apps.nexuslauncher")
        )

        val issues = rule.check(listOf(root))

        assertEquals(1, issues.size)
        assertEquals(rule.id, issues.single().ruleId)
        assertTrue(issues.single().message?.contains("com.google.android.apps.nexuslauncher") == true)
        assertTrue(issues.single().message?.contains("com.uniboard") == true)
    }

    @Test
    fun `ignores runtime snapshot from expected package`() {
        val rule = RuntimeSystemAppSnapshotWarningRule(expectedPackageName = "com.uniboard")
        val root = runtimeNode(
            packageName = "com.uniboard",
            child = runtimeNode(packageName = "com.uniboard")
        )

        val issues = rule.check(listOf(root))

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `ignores snapshot when expected package is unknown`() {
        val rule = RuntimeSystemAppSnapshotWarningRule(expectedPackageName = null)
        val root = runtimeNode(packageName = "com.google.android.apps.nexuslauncher")

        val issues = rule.check(listOf(root))

        assertTrue(issues.isEmpty())
    }

    private fun runtimeNode(
        packageName: String,
        child: UiComponent? = null
    ): UiComponent {
        return UiComponent(
            id = null,
            type = "View",
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = "runtime.xml",
            treePath = "/View[0]",
            properties = UiProperties(
                rawAttributes = mapOf("package" to packageName)
            ),
            children = listOfNotNull(child)
        )
    }
}
