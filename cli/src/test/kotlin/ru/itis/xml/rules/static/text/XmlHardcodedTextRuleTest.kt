package ru.itis.xml.rules.static.text

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class XmlHardcodedTextRuleTest {

    @Test
    fun `reports hardcoded text for xml components`() {
        val component = component(
            type = ComponentTypes.TEXT_VIEW,
            sourceType = SourceType.XML,
            text = "Hardcoded title"
        )

        val issues = XmlHardcodedTextRule().check(listOf(component))

        assertTrue(
            issues.any { issue -> issue.componentType == ComponentTypes.TEXT_VIEW },
            "Expected XML hardcoded text to be reported"
        )
    }

    @Test
    fun `does not report hardcoded text for compose components`() {
        val component = component(
            type = ComponentTypes.COMPOSE_TEXT,
            sourceType = SourceType.COMPOSE,
            text = "Hardcoded title"
        )

        val issues = XmlHardcodedTextRule().check(listOf(component))

        assertFalse(
            issues.any { issue -> issue.componentType == ComponentTypes.COMPOSE_TEXT },
            "XML hardcoded text rule should not report Compose components"
        )
    }

    private fun component(
        type: String,
        sourceType: SourceType,
        text: String
    ): UiComponent {
        return UiComponent(
            id = null,
            type = type,
            sourceType = sourceType,
            filePath = "demo",
            properties = UiProperties(text = text)
        )
    }
}
