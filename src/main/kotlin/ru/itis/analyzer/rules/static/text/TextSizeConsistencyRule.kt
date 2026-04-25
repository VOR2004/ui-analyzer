package ru.itis.analyzer.rules.static.text

import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.source.xml.resource.ResourceRepository
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.rules.base.onlyXmlFlatComponents
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.UiComponent

class TextSizeConsistencyRule(
    private val resourceRepository: ResourceRepository = ResourceRepository.empty()
) : Rule {
    override val id: String = AnalyzerStrings.RuleIds.TEXT_SIZE_CONSISTENCY

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        val flat = components.onlyXmlFlatComponents()
        val textViews = flat.filter {
            it.type.endsWith(ComponentTypes.TEXT_VIEW_SUFFIX) || it.type == ComponentTypes.TEXT_VIEW
        }

        val sizes = textViews
            .mapNotNull { component ->
                component.properties.textSize?.let { size ->
                    component to resolveDimension(size)
                }
            }

        if (sizes.isEmpty()) return emptyList()

        val grouped = sizes.groupBy({ it.second }, { it.first })
        if (grouped.size <= 1) return emptyList()

        val dominantSize = grouped.maxByOrNull { it.value.size }?.key

        return sizes
            .filter { (_, size) -> size != dominantSize }
            .map { (component, size) ->
                AnalysisIssue(
                    ruleId = id,
                    severity = Severity.WARNING,
                    componentId = component.id,
                    componentType = component.type,
                    filePath = component.filePath,
                    message = AnalyzerStrings.Messages.textSizeConsistency(size, dominantSize),
                    recommendation = AnalyzerStrings.Messages.TEXT_SIZE_CONSISTENCY_RECOMMENDATION
                )
            }
    }

    private fun resolveDimension(value: String): String {
        return resourceRepository.resolveDimension(value) ?: value
    }
}
