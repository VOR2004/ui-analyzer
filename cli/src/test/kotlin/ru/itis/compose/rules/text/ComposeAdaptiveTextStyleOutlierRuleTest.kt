package ru.itis.compose.rules.text
import ru.itis.analyzer.messages.rules.RuleIds

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeAdaptiveTextStyleOutlierRuleTest {

    @Test
    fun `reports compose text style outlier compared with dominant predicted role style`() {
        val components = listOf(
            composeText("body1", UiProperties(typographyStyle = "MaterialTheme.typography.bodyMedium")),
            composeText("body2", UiProperties(typographyStyle = "MaterialTheme.typography.bodyMedium")),
            composeText("body3", UiProperties(typographyStyle = "MaterialTheme.typography.bodyMedium")),
            composeText("bodyOutlier", UiProperties(typographyStyle = "MaterialTheme.typography.bodyLarge"))
        )

        val issues = ComposeAdaptiveTextStyleOutlierRule().check(components)

        assertEquals(1, issues.size)
        assertEquals(RuleIds.COMPOSE_ADAPTIVE_TEXT_STYLE_OUTLIER, issues.single().ruleId)
        assertEquals("bodyOutlier", issues.single().componentId)
        assertEquals(Severity.INFO, issues.single().severity)
        assertTrue(issues.single().recommendation.contains("predictedRole=BODY"))
        assertTrue(issues.single().message.orEmpty().contains("typographyStyle"))
    }

    @Test
    fun `uses warning severity for multiple compose text style differences`() {
        val components = listOf(
            composeText("body1", UiProperties(textSize = "16.sp", textStyle = "FontWeight.Normal")),
            composeText("body2", UiProperties(textSize = "16.sp", textStyle = "FontWeight.Normal")),
            composeText("body3", UiProperties(textSize = "16.sp", textStyle = "FontWeight.Normal")),
            composeText("accent", UiProperties(textSize = "18.sp", textStyle = "FontWeight.Bold"))
        )

        val issues = ComposeAdaptiveTextStyleOutlierRule().check(components)

        assertEquals(1, issues.size)
        assertEquals(Severity.WARNING, issues.single().severity)
    }

    @Test
    fun `does not compare different predicted text roles`() {
        val components = listOf(
            composeText("body1", UiProperties(typographyStyle = "MaterialTheme.typography.bodyMedium")),
            composeText("body2", UiProperties(typographyStyle = "MaterialTheme.typography.bodyMedium")),
            composeText("body3", UiProperties(typographyStyle = "MaterialTheme.typography.bodyMedium")),
            composeText("title", UiProperties(typographyStyle = "MaterialTheme.typography.titleLarge"))
        )

        val issues = ComposeAdaptiveTextStyleOutlierRule().check(components)

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `does not report without repeated dominant compose style`() {
        val components = listOf(
            composeText("title", UiProperties(typographyStyle = "MaterialTheme.typography.titleLarge")),
            composeText("body", UiProperties(typographyStyle = "MaterialTheme.typography.bodyMedium")),
            composeText("label", UiProperties(typographyStyle = "MaterialTheme.typography.labelSmall")),
            composeText("caption", UiProperties(typographyStyle = "MaterialTheme.typography.bodySmall"))
        )

        val issues = ComposeAdaptiveTextStyleOutlierRule().check(components)

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
}


