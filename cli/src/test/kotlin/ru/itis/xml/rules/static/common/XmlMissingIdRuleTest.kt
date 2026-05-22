package ru.itis.xml.rules.static.common

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.itis.analyzer.config.ComponentTypes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class XmlMissingIdRuleTest {

    @Test
    fun `reports missing id for xml components`() {
        val component = component(
            type = ComponentTypes.TEXT_VIEW,
            sourceType = SourceType.XML
        )

        val issues = XmlMissingIdRule().check(listOf(component))

        assertTrue(
            issues.any { issue -> issue.componentType == ComponentTypes.TEXT_VIEW },
            "Expected XML TextView without id to be reported"
        )
    }

    @Test
    fun `does not report missing id for compose components`() {
        val component = component(
            type = ComponentTypes.COMPOSE_COLUMN,
            sourceType = SourceType.COMPOSE,
            children = listOf(
                component(type = ComponentTypes.COMPOSE_TEXT, sourceType = SourceType.COMPOSE),
                component(type = ComponentTypes.COMPOSE_BUTTON, sourceType = SourceType.COMPOSE)
            )
        )

        val issues = XmlMissingIdRule().check(listOf(component))

        assertFalse(
            issues.any { issue -> issue.componentType in composeTypes },
            "Compose components should not require android:id"
        )
    }

    private fun component(
        type: String,
        sourceType: SourceType,
        children: List<UiComponent> = emptyList()
    ): UiComponent {
        return UiComponent(
            id = null,
            type = type,
            sourceType = sourceType,
            filePath = "demo",
            properties = UiProperties(),
            children = children
        )
    }

    private companion object {
        val composeTypes = setOf(
            ComponentTypes.COMPOSE_COLUMN,
            ComponentTypes.COMPOSE_TEXT,
            ComponentTypes.COMPOSE_BUTTON
        )
    }
}
