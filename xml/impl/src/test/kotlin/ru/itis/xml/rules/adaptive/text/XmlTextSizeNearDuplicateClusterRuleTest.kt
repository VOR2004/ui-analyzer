package ru.itis.xml.rules.adaptive.text
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

class XmlTextSizeNearDuplicateClusterRuleTest {

    @Test
    fun `reports near duplicate xml text sizes inside predicted role`() {
        val components = listOf(
            xmlText("body1", UiProperties(textSize = "16sp")),
            xmlText("body2", UiProperties(textSize = "16sp")),
            xmlText("body3", UiProperties(textSize = "15sp"))
        )

        val issues = XmlTextSizeNearDuplicateClusterRule().check(context(components))

        assertEquals(1, issues.size)
        assertEquals(RuleIds.XML_TEXT_SIZE_NEAR_DUPLICATE_CLUSTER, issues.single().ruleId)
        assertEquals("body3", issues.single().componentId)
        assertTrue(issues.single().recommendation.contains("predictedRole=BODY"))
    }

    @Test
    fun `resolves dimen values before comparing xml text sizes`() {
        val components = listOf(
            xmlText("body1", UiProperties(textSize = "16sp")),
            xmlText("body2", UiProperties(textSize = "16sp")),
            xmlText("body3", UiProperties(textSize = "@dimen/almost_body_text"))
        )
        val repository = DefaultResourceRepository(
            colors = emptyMap(),
            dimensions = mapOf("almost_body_text" to "15sp")
        )

        val issues = XmlTextSizeNearDuplicateClusterRule().check(context(components, repository))

        assertEquals(1, issues.size)
        assertEquals("body3", issues.single().componentId)
    }

    @Test
    fun `does not compare near duplicate text sizes across predicted roles`() {
        val components = listOf(
            xmlText("title", UiProperties(textSize = "20sp", textStyle = "bold")),
            xmlText("body1", UiProperties(textSize = "19sp")),
            xmlText("body2", UiProperties(textSize = "19sp"))
        )

        val issues = XmlTextSizeNearDuplicateClusterRule().check(context(components))

        assertTrue(issues.isEmpty())
    }

    @Test
    fun `does not report compose text sizes`() {
        val components = listOf(
            composeText("body1", UiProperties(textSize = "16.sp")),
            composeText("body2", UiProperties(textSize = "16.sp")),
            composeText("body3", UiProperties(textSize = "15.sp"))
        )

        val issues = XmlTextSizeNearDuplicateClusterRule().check(context(components))

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

    private fun xmlText(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = "TextView",
            sourceType = SourceType.XML,
            filePath = "demo.xml",
            properties = properties
        )
    }

    private fun composeText(id: String, properties: UiProperties): UiComponent {
        return UiComponent(
            id = id,
            type = "Text",
            sourceType = SourceType.COMPOSE,
            filePath = "Demo.kt",
            properties = properties
        )
    }
}

