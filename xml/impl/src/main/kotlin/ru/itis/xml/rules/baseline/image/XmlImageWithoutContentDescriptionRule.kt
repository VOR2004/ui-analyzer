package ru.itis.xml.rules.baseline.image
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.config.components.ResourcePatterns
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class XmlImageWithoutContentDescriptionRule : Rule {

    override val id: String = RuleIds.IMAGE_WITHOUT_CONTENT_DESCRIPTION

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.findImageComponents(components)
            .filter { component -> component.sourceType == SourceType.XML }
            .filter { shouldHaveContentDescription(it) }
            .filter { hasMissingAccessibleDescription(it) }
            .map { component ->
                AnalysisIssue(
                    ruleId = id,
                    severity = Severity.WARNING,
                    componentId = component.id,
                    componentType = component.type,
                    filePath = component.filePath,
                    message = AnalyzerMessages.imageWithoutContentDescription(component.type),
                    recommendation = AnalyzerMessages.IMAGE_WITHOUT_CONTENT_DESCRIPTION_RECOMMENDATION
                )
            }
    }

    private fun shouldHaveContentDescription(component: UiComponent): Boolean {
        return when (component.type) {
            ComponentTypes.IMAGE_VIEW,
            ComponentTypes.IMAGE_BUTTON,
            ComponentTypes.ANDROID_WIDGET_IMAGE_VIEW,
            ComponentTypes.ANDROID_WIDGET_IMAGE_BUTTON,
            ComponentTypes.APP_COMPAT_IMAGE_VIEW,
            ComponentTypes.APP_COMPAT_IMAGE_BUTTON,
            ComponentTypes.ANDROIDX_APP_COMPAT_IMAGE_VIEW,
            ComponentTypes.ANDROIDX_APP_COMPAT_IMAGE_BUTTON -> true

            else -> component.type.endsWith(ComponentTypes.IMAGE_VIEW_SUFFIX) ||
                    component.type.endsWith(ComponentTypes.IMAGE_BUTTON_SUFFIX)
        }
    }

    private fun hasMissingAccessibleDescription(component: UiComponent): Boolean {
        val contentDescription = component.properties.contentDescription?.trim()

        if (contentDescription.isNullOrBlank()) {
            return true
        }

        if (contentDescription == ResourcePatterns.NULL_REF && isImageButton(component)) {
            return true
        }

        return false
    }

    private fun isImageButton(component: UiComponent): Boolean {
        return component.type == ComponentTypes.IMAGE_BUTTON ||
            component.type == ComponentTypes.ANDROID_WIDGET_IMAGE_BUTTON ||
            component.type == ComponentTypes.APP_COMPAT_IMAGE_BUTTON ||
            component.type == ComponentTypes.ANDROIDX_APP_COMPAT_IMAGE_BUTTON ||
            component.type.endsWith(ComponentTypes.IMAGE_BUTTON_SUFFIX)
    }
}


