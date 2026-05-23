package ru.itis.xml.rules.static.accessibility
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.rules.base.onlyXmlFlatComponents
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class XmlTouchTargetTooSmallRule(
    private val resourceRepository: ResourceRepository = ResourceRepository.empty()
) : Rule {
    override val id: String = RuleIds.TOUCH_TARGET_TOO_SMALL

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return components.onlyXmlFlatComponents()
            .filter { component -> isInteractive(component) }
            .mapNotNull { component -> createIssueIfNeeded(component) }
    }

    private fun createIssueIfNeeded(component: UiComponent): AnalysisIssue? {
        val width = DimensionUtils.parseDp(resolveDimension(component.properties.width))
        val height = DimensionUtils.parseDp(resolveDimension(component.properties.height))

        val widthTooSmall = width != null && width < MIN_TOUCH_TARGET_DP
        val heightTooSmall = height != null && height < MIN_TOUCH_TARGET_DP

        if (!widthTooSmall && !heightTooSmall) {
            return null
        }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = component.id,
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerMessages.touchTargetTooSmall(
                width = component.properties.width,
                height = component.properties.height
            ),
            recommendation = AnalyzerMessages.TOUCH_TARGET_TOO_SMALL_RECOMMENDATION
        )
    }

    private fun isInteractive(component: UiComponent): Boolean {
        val type = component.type
        return type == ComponentTypes.BUTTON ||
            type == ComponentTypes.MATERIAL_BUTTON ||
            type.endsWith(ComponentTypes.BUTTON_SUFFIX) ||
            type == ComponentTypes.IMAGE_BUTTON ||
            type.endsWith(ComponentTypes.IMAGE_BUTTON_SUFFIX)
    }

    private fun resolveDimension(value: String?): String? {
        return resourceRepository.resolveDimension(value) ?: value
    }

    private companion object {
        const val MIN_TOUCH_TARGET_DP = 48f
    }
}


