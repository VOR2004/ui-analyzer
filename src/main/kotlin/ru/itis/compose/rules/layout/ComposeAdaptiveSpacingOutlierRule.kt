package ru.itis.compose.rules.layout

import kotlin.math.abs
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class ComposeAdaptiveSpacingOutlierRule : Rule {
    override val id: String = AnalyzerStrings.RuleIds.COMPOSE_ADAPTIVE_SPACING_OUTLIER

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val composeComponents = ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType == SourceType.COMPOSE }

        val commonValuesByFile = composeComponents
            .groupBy { component -> component.filePath }
            .mapValues { (_, fileComponents) ->
                findCommonValues(fileComponents.flatMap { component -> collectSpacingValues(component) })
            }

        return composeComponents.flatMap { component ->
            val commonValues = commonValuesByFile[component.filePath].orEmpty()
            if (commonValues.isEmpty()) {
                emptyList()
            } else {
                collectSpacingProperties(component).mapNotNull { property ->
                    if (isOutlier(property.value, commonValues)) {
                        createIssue(component, property, commonValues)
                    } else {
                        null
                    }
                }
            }
        }
    }

    private fun collectSpacingValues(component: UiComponent): List<Float> {
        return collectSpacingProperties(component).map { property -> property.value }
    }

    private fun collectSpacingProperties(component: UiComponent): List<SpacingProperty> {
        return listOfNotNull(
            parseSpacing(AnalyzerStrings.PropertyNames.PADDING, component.properties.padding),
            parseSpacing(AnalyzerStrings.PropertyNames.WIDTH, component.properties.width),
            parseSpacing(AnalyzerStrings.PropertyNames.HEIGHT, component.properties.height)
        )
    }

    private fun parseSpacing(propertyName: String, rawValue: String?): SpacingProperty? {
        val value = DimensionUtils.parseDp(rawValue) ?: return null
        return SpacingProperty(propertyName = propertyName, value = value)
    }

    private fun findCommonValues(values: List<Float>): List<Float> {
        if (values.size < MIN_VALUES_PER_FILE) {
            return emptyList()
        }

        return values
            .groupingBy { value -> value }
            .eachCount()
            .filter { (_, count) -> count >= MIN_COMMON_VALUE_FREQUENCY }
            .keys
            .sorted()
    }

    private fun isOutlier(value: Float, commonValues: List<Float>): Boolean {
        return commonValues.none { commonValue -> abs(commonValue - value) <= SPACING_TOLERANCE_DP }
    }

    private fun createIssue(
        component: UiComponent,
        property: SpacingProperty,
        commonValues: List<Float>
    ): AnalysisIssue {
        val expected = commonValues.joinToString(", ") { value -> "${value}dp" }

        return AnalysisIssue(
            ruleId = id,
            severity = Severity.WARNING,
            componentId = component.id,
            componentLocator = component.treePath?.let { path -> "${component.type}[path=$path]" },
            componentType = component.type,
            filePath = component.filePath,
            message = AnalyzerStrings.Messages.composeAdaptiveSpacingOutlier(
                propertyName = property.propertyName,
                actualValue = property.value
            ),
            recommendation = AnalyzerStrings.Messages.composeAdaptiveSpacingOutlierRecommendation(expected)
        )
    }

    private data class SpacingProperty(
        val propertyName: String,
        val value: Float
    )

    private companion object {
        const val MIN_VALUES_PER_FILE = 4
        const val MIN_COMMON_VALUE_FREQUENCY = 2
        const val SPACING_TOLERANCE_DP = 2f
    }
}
