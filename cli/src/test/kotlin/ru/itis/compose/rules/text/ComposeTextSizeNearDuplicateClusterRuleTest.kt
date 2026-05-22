package ru.itis.compose.rules.text

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeTextSizeNearDuplicateClusterRuleTest {

    @Test
    fun `reports near duplicate compose text sizes inside predicted role`() {
        val components = listOf(
            composeText("body1", UiProperties(textSize = "16.sp")),
            composeText("body2", UiProperties(textSize = "16.sp")),
            composeText("body3", UiProperties(textSize = "15.sp"))
        )

        val issues = ComposeTextSizeNearDuplicateClusterRule().check(components)

        assertEquals(1, issues.size)
        assertEquals(AnalyzerStrings.RuleIds.COMPOSE_TEXT_SIZE_NEAR_DUPLICATE_CLUSTER, issues.single().ruleId)
        assertEquals("body3", issues.single().componentId)
        assertTrue(issues.single().recommendation.contains("predictedRole=BODY"))
    }

    @Test
    fun `does not compare near duplicate sizes across predicted roles`() {
        val components = listOf(
            composeText("title", UiProperties(textSize = "20.sp", textStyle = "FontWeight.Bold")),
            composeText("body1", UiProperties(textSize = "19.sp")),
            composeText("body2", UiProperties(textSize = "19.sp"))
        )

        val issues = ComposeTextSizeNearDuplicateClusterRule().check(components)

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `does not report xml text sizes`() {
        val components = listOf(
            xmlText("body1", UiProperties(textSize = "16sp")),
            xmlText("body2", UiProperties(textSize = "16sp")),
            xmlText("body3", UiProperties(textSize = "15sp"))
        )

        val issues = ComposeTextSizeNearDuplicateClusterRule().check(components)

        assertTrue(issues.isEmpty())
    }

    private fun composeText(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.COMPOSE_TEXT,
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            treePath = "/Text[$id]",
            properties = properties
        )
    }

    private fun xmlText(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = ComponentTypes.TEXT_VIEW,
            sourceType = SourceType.XML,
            filePath = "demo.xml",
            properties = properties
        )
    }
}
