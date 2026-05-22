package ru.itis.analyzer.rules.base

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.xml.rules.static.accessibility.XmlTouchTargetTooSmallRule
import ru.itis.xml.rules.static.color.XmlHardcodedColorRule
import ru.itis.xml.rules.static.common.XmlHardcodedDimensionRule
import ru.itis.xml.rules.static.text.XmlTextSizeConsistencyRule
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class SourceTypeRuleFilteringTest {

    @Test
    fun `xml-only rules ignore compose components`() {
        val composeComponent = component(
            type = ComponentTypes.COMPOSE_BUTTON,
            sourceType = SourceType.COMPOSE,
            properties = UiProperties(
                width = "32.dp",
                height = "32.dp",
                backgroundColor = "#FF0000",
                textSize = "32.sp"
            )
        )

        assertFalse(XmlHardcodedDimensionRule().check(listOf(composeComponent)).hasIssuesFor(composeComponent))
        assertFalse(XmlHardcodedColorRule().check(listOf(composeComponent)).hasIssuesFor(composeComponent))
        assertFalse(XmlTouchTargetTooSmallRule().check(listOf(composeComponent)).hasIssuesFor(composeComponent))
        assertFalse(XmlTextSizeConsistencyRule().check(listOf(composeComponent)).hasIssuesFor(composeComponent))
    }

    @Test
    fun `xml-only rules still analyze xml components`() {
        val xmlComponent = component(
            type = ComponentTypes.BUTTON,
            sourceType = SourceType.XML,
            properties = UiProperties(
                width = "32dp",
                height = "32dp",
                backgroundColor = "#FF0000",
                textSize = "32sp"
            )
        )

        assertTrue(XmlHardcodedDimensionRule().check(listOf(xmlComponent)).hasIssuesFor(xmlComponent))
        assertTrue(XmlHardcodedColorRule().check(listOf(xmlComponent)).hasIssuesFor(xmlComponent))
        assertTrue(XmlTouchTargetTooSmallRule().check(listOf(xmlComponent)).hasIssuesFor(xmlComponent))
    }

    private fun component(
        type: String,
        sourceType: SourceType,
        properties: UiProperties
    ): UiComponent {
        return UiComponent(
            id = null,
            type = type,
            sourceType = sourceType,
            filePath = "demo",
            properties = properties
        )
    }

    private fun List<ru.itis.model.AnalysisIssue>.hasIssuesFor(component: UiComponent): Boolean {
        return any { issue ->
            issue.componentType == component.type &&
                issue.filePath == component.filePath
        }
    }
}
