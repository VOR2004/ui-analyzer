package ru.itis.compose.rules.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.model.RuntimeAttributes
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiBounds
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class RuntimeScreenDensitySnapshotInfoRuleTest {
    private val rule = RuntimeScreenDensitySnapshotInfoRule()

    @Test
    fun `reports compose runtime snapshot metadata`() {
        val root = runtimeRoot(
            sourceType = SourceType.COMPOSE_RUNTIME,
            bounds = UiBounds(x = 0f, y = 0f, width = 1080f, height = 2400f),
            attributes = mapOf(
                RuntimeAttributes.SCREEN to "BoardScreen",
                RuntimeAttributes.STATE to "content",
                RuntimeAttributes.DENSITY to "2.75",
                RuntimeAttributes.DENSITY_DPI to "440",
                RuntimeAttributes.ORIENTATION to "portrait"
            )
        )

        val issues = rule.check(listOf(root))

        assertEquals(1, issues.size)
        assertEquals(RuleIds.RUNTIME_SCREEN_DENSITY_SNAPSHOT_INFO, issues.single().ruleId)
        assertEquals(Severity.INFO, issues.single().severity)
        assertTrue(issues.single().message?.contains("BoardScreen") == true)
        assertTrue(issues.single().message?.contains("density=2.75") == true)
    }

    @Test
    fun `infers android runtime snapshot size and orientation from bounds`() {
        val root = runtimeRoot(
            sourceType = SourceType.ANDROID_RUNTIME,
            bounds = UiBounds(x = 0f, y = 0f, width = 1920f, height = 1080f)
        )

        val issues = rule.check(listOf(root))

        assertEquals(1, issues.size)
        assertTrue(issues.single().message?.contains("1920px x 1080px") == true)
        assertTrue(issues.single().message?.contains("landscape") == true)
    }

    private fun runtimeRoot(
        sourceType: SourceType,
        bounds: UiBounds,
        attributes: Map<String, String> = emptyMap()
    ): UiComponent {
        return UiComponent(
            id = "root",
            type = "Root",
            sourceType = sourceType,
            filePath = "runtime.xml",
            treePath = "/Root[0]",
            properties = UiProperties(
                bounds = bounds,
                rawAttributes = attributes
            )
        )
    }
}
