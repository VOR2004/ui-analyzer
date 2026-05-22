package ru.itis.xml.rules.static.structure
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds

import ru.itis.analyzer.rules.base.Rule
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

class XmlDeepLayoutNestingRule : Rule {
    override val id: String = RuleIds.DEEP_LAYOUT_NESTING

    override fun check(components: List<UiComponent>): List<AnalysisIssue> {
        return components
            .filter { root -> root.sourceType == SourceType.XML }
            .mapNotNull { root ->
                val deepest = findDeepestComponent(root)
                if (deepest.depth <= MAX_RECOMMENDED_DEPTH) {
                    return@mapNotNull null
                }

                AnalysisIssue(
                    ruleId = id,
                    severity = Severity.WARNING,
                    componentId = deepest.component.id,
                    componentType = deepest.component.type,
                    filePath = deepest.component.filePath,
                    message = AnalyzerMessages.deepLayoutNesting(deepest.depth),
                    recommendation = AnalyzerMessages.DEEP_LAYOUT_NESTING_RECOMMENDATION
                )
            }
    }

    private fun findDeepestComponent(root: UiComponent): ComponentDepth {
        return collectDepths(root, depth = 1).maxBy { it.depth }
    }

    private fun collectDepths(component: UiComponent, depth: Int): List<ComponentDepth> {
        return listOf(ComponentDepth(component, depth)) +
            component.children.flatMap { child -> collectDepths(child, depth + 1) }
    }

    private data class ComponentDepth(
        val component: UiComponent,
        val depth: Int
    )

    private companion object {
        const val MAX_RECOMMENDED_DEPTH = 8
    }
}


