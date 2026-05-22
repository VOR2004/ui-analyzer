package ru.itis.compose.rules.text
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeTooManyTextStylesOnScreenRule : Rule {
    override val id: String = RuleIds.COMPOSE_TOO_MANY_TEXT_STYLES_ON_SCREEN

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType == SourceType.COMPOSE }
            .filter { component -> component.type == ComponentTypes.COMPOSE_TEXT }
            .groupBy { component -> component.filePath }
            .mapNotNull { (filePath, textComponents) ->
                analyzeFile(filePath, textComponents)
            }
    }

    private fun analyzeFile(
        filePath: String,
        textComponents: List<UiComponent>
    ): AnalysisIssue? {
        val signatures = textComponents.mapNotNull { component -> TextSignature.from(component) }
        if (signatures.size < MIN_TEXTS_PER_FILE) {
            return null
        }

        val styleCounts = signatures.groupingBy { signature -> signature }.eachCount()
        val dominantCount = styleCounts.maxOfOrNull { (_, count) -> count } ?: return null
        val repeatedStyleCount = styleCounts.count { (_, count) -> count >= MIN_STYLE_FREQUENCY }
        val dominantShare = dominantCount.toDouble() / signatures.size

        val isFragmented =
            repeatedStyleCount > MAX_REPEATED_STYLES_PER_FILE ||
                dominantShare < MIN_DOMINANT_SHARE

        if (!isFragmented) {
            return null
        }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.INFO,
            componentId = null,
            componentLocator = "ComposeFile[path=$filePath]",
            componentType = "ComposeFile",
            filePath = filePath,
            message = AnalyzerMessages.composeTooManyTextStylesOnScreen(
                actualCount = styleCounts.size,
                dominantSharePercent = (dominantShare * 100).toInt()
            ),
            recommendation = AnalyzerMessages.COMPOSE_TOO_MANY_TEXT_STYLES_ON_SCREEN_RECOMMENDATION
        )
    }

    private data class TextSignature(
        val typographyStyle: String?,
        val textSize: String?,
        val textStyle: String?,
        val fontFamily: String?
    ) {
        companion object {
            fun from(component: UiComponent): TextSignature? {
                val signature = TextSignature(
                    typographyStyle = component.properties.typographyStyle?.trim(),
                    textSize = component.properties.textSize?.trim(),
                    textStyle = component.properties.textStyle?.trim(),
                    fontFamily = component.properties.fontFamily?.trim()
                )

                return signature.takeIf { value ->
                    value.textSize != null || value.textStyle != null || value.fontFamily != null
                        || value.typographyStyle != null
                }
            }
        }
    }

    private companion object {
        const val MIN_TEXTS_PER_FILE = 4
        const val MIN_STYLE_FREQUENCY = 2
        const val MAX_REPEATED_STYLES_PER_FILE = 2
        const val MIN_DOMINANT_SHARE = 0.6
    }
}


