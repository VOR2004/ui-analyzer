package ru.itis.xml.rules.adaptive.layout
import ru.itis.analyzer.messages.rules.RuleIds

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.core.AnalysisContext
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties
import ru.itis.xml.source.resource.DefaultResourceRepository
import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.xml.style.profile.ProjectStyleProfile
import ru.itis.xml.style.profile.SpacingScale

class XmlNearDuplicateSpacingClusterRuleTest {

    @Test
    fun `reports near duplicate xml spacing values`() {
        val components = listOf(
            xmlComponent("view1", UiProperties(padding = "16dp")),
            xmlComponent("view2", UiProperties(padding = "16dp")),
            xmlComponent("view3", UiProperties(padding = "15dp"))
        )

        val issues = XmlNearDuplicateSpacingClusterRule().check(context(components))

        assertEquals(1, issues.size)
        assertEquals(RuleIds.XML_NEAR_DUPLICATE_SPACING_CLUSTER, issues.single().ruleId)
        assertEquals("view3", issues.single().componentId)
        assertTrue(issues.single().recommendation.contains("16.0dp"))
    }

    @Test
    fun `resolves dimen values before comparing xml spacing`() {
        val components = listOf(
            xmlComponent("view1", UiProperties(padding = "16dp")),
            xmlComponent("view2", UiProperties(padding = "16dp")),
            xmlComponent("view3", UiProperties(padding = "@dimen/almost_spacing"))
        )
        val repository = DefaultResourceRepository(
            colors = emptyMap(),
            dimensions = mapOf("almost_spacing" to "15dp")
        )

        val issues = XmlNearDuplicateSpacingClusterRule().check(context(components, repository))

        assertEquals(1, issues.size)
        assertEquals("view3", issues.single().componentId)
    }

    @Test
    fun `does not report compose spacing values`() {
        val components = listOf(
            composeComponent("box1", UiProperties(padding = "16.dp")),
            composeComponent("box2", UiProperties(padding = "16.dp")),
            composeComponent("box3", UiProperties(padding = "15.dp"))
        )

        val issues = XmlNearDuplicateSpacingClusterRule().check(context(components))

        assertTrue(issues.isEmpty())
    }

    private fun context(
        components: List<UiComponent>,
        resourceRepository: ResourceRepository = ResourceRepository.empty()
    ): AnalysisContext {
        return AnalysisContext(
            components = components,
            resourceRepository = resourceRepository,
            projectStyleProfile = emptyProjectStyleProfile(),
            screenProfiles = emptyMap()
        )
    }

    private fun emptyProjectStyleProfile(): ProjectStyleProfile {
        return ProjectStyleProfile(
            textSizeClusters = emptyList(),
            textSizeClustersByRole = emptyMap(),
            paddingClusters = emptyList(),
            marginClusters = emptyList(),
            spacingScale = SpacingScale(
                baseUnitDp = null,
                commonValuesDp = emptyList(),
                dominantSpacingDp = null
            ),
            dominantButtonStyle = null,
            dominantTextStyle = null,
            dominantTextStylesByRole = emptyMap()
        )
    }

    private fun xmlComponent(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = "TextView",
            sourceType = SourceType.XML,
            filePath = "demo.xml",
            properties = properties
        )
    }

    private fun composeComponent(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = "Box",
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            properties = properties
        )
    }
}

