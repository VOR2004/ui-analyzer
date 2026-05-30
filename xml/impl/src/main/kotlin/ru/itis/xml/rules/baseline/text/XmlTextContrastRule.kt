package ru.itis.xml.rules.baseline.text
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.config.analyzer.AnalyzerThresholds
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.rules.base.onlyXmlRoots
import ru.itis.analyzer.utils.ContrastUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.analyzer.utils.TreeUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.TreeNodeContext
import ru.itis.model.UiComponent
import java.lang.String.format
import java.util.Locale

class XmlTextContrastRule(
    private val resourceRepository: ResourceRepository
) : Rule {

    override val id: String = RuleIds.TEXT_CONTRAST

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val issues = mutableListOf<AnalysisIssue>()
        val effectiveBackgrounds = mutableMapOf<UiComponent, String?>()

        TreeUtils.traverse(components.onlyXmlRoots()) { context ->
            val effectiveBackground = resolveEffectiveBackground(context, effectiveBackgrounds)
            effectiveBackgrounds[context.component] = effectiveBackground

            if (isTextualComponent(context.component)) {
                val issue = checkTextContrast(context.component, effectiveBackground)
                if (issue != null) {
                    issues += issue
                }
            }
        }

        return issues
    }

    private fun resolveEffectiveBackground(
        context: TreeNodeContext,
        effectiveBackgrounds: Map<UiComponent, String?>
    ): String? {
        val component = context.component

        val ownBackgroundCandidate = component.properties.backgroundTint
            ?: component.properties.backgroundColor

        val ownBackground = resourceRepository.resolveColor(ownBackgroundCandidate)
        if (ownBackground != null) return ownBackground

        val parent = context.parent ?: return null
        return effectiveBackgrounds[parent]
    }

    private fun checkTextContrast(
        component: UiComponent,
        effectiveBackground: String?
    ): AnalysisIssue? {
        val textColorRaw = component.properties.textColor ?: return null
        val backgroundColor = effectiveBackground ?: return null

        val textColor = resourceRepository.resolveColor(textColorRaw) ?: return null

        val contrast = ContrastUtils.calculateContrastRatio(textColor, backgroundColor)
            ?: return null

        val textSize = DimensionUtils.parseSp(
            resourceRepository.resolveDimension(component.properties.textSize)
                ?: component.properties.textSize
        )
        val minContrast = if (textSize != null && textSize >= AnalyzerThresholds.LARGE_TEXT_THRESHOLD_SP) {
            AnalyzerThresholds.LARGE_TEXT_MIN_CONTRAST
        } else {
            AnalyzerThresholds.NORMAL_TEXT_MIN_CONTRAST
        }

        if (contrast >= minContrast) return null

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = component.id,
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerMessages.insufficientTextContrast(
                textColor = textColor,
                backgroundColor = backgroundColor,
                ratio = formatRatio(contrast),
                minContrast = minContrast
            ),
            recommendation = AnalyzerMessages.INSUFFICIENT_TEXT_CONTRAST_RECOMMENDATION
        )
    }

    private fun isTextualComponent(component: UiComponent): Boolean {
        return component.type == ComponentTypes.TEXT_VIEW ||
                component.type.endsWith(ComponentTypes.TEXT_VIEW_SUFFIX) ||
                component.type == ComponentTypes.BUTTON ||
                component.type.endsWith(ComponentTypes.BUTTON_SUFFIX) ||
                component.type == ComponentTypes.MATERIAL_BUTTON
    }

    private fun formatRatio(value: Double): String {
        return format(Locale.US, "%.2f", value)
    }
}


