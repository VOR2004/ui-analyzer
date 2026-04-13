package ru.itis.analyzer.rules.static.image

import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.config.ResourcePatterns
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class ImageWithoutContentDescriptionRule : Rule {

    override val id: String = AnalyzerStrings.RuleIds.IMAGE_WITHOUT_CONTENT_DESCRIPTION

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.findImageComponents(components)
            .filter { shouldHaveContentDescription(it) }
            .filter { hasMissingAccessibleDescription(it) }
            .map { component ->
                AnalysisIssue(
                    ruleId = id,
                    severity = Severity.WARNING,
                    componentId = component.id,
                    componentType = component.type,
                    filePath = component.filePath,
                    message = AnalyzerStrings.Messages.imageWithoutContentDescription(component.type),
                    recommendation = AnalyzerStrings.Messages.IMAGE_WITHOUT_CONTENT_DESCRIPTION_RECOMMENDATION
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
