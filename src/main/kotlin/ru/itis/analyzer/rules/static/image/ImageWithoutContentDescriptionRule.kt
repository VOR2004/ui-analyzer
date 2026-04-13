package ru.itis.analyzer.rules.static.image

import ru.itis.analyzer.config.ComponentTypes
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
            .filter { it.properties.contentDescription.isNullOrBlank() }
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
}
