package ru.itis.xml.rules.adaptive.text

import ru.itis.analyzer.core.AnalysisContext
import ru.itis.analyzer.messages.analyzer.AnalyzerMessages
import ru.itis.analyzer.messages.rules.RuleIds
import ru.itis.analyzer.rules.base.ContextualRule
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.analyzer.utils.DimensionUtils
import ru.itis.analyzer.utils.NearDuplicateDimensionAnalyzer
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.xml.style.role.DefaultTextRolePredictor
import ru.itis.xml.style.signature.PredictedTextRole
import ru.itis.xml.style.signature.TextRolePredictor

class XmlTextSizeNearDuplicateClusterRule : ContextualRule {
    override val id: String = RuleIds.XML_TEXT_SIZE_NEAR_DUPLICATE_CLUSTER

    private val rolePredictor: TextRolePredictor = DefaultTextRolePredictor()

    override fun check(components: List<UiComponent>): List<AnalysisIssue> = emptyList()

    override fun check(context: AnalysisContext): List<AnalysisIssue> {
        val entries = ComponentUtils.findTextViews(context.components)
            .filter { component -> component.sourceType == SourceType.XML }
            .mapNotNull { component -> collectEntry(context, component) }

        return entries
            .groupBy { entry -> entry.fileRoleKey }
            .flatMap { (_, roleEntries) -> analyzeRole(roleEntries) }
    }

    private fun collectEntry(
        context: AnalysisContext,
        component: UiComponent
    ): TextSizeEntry? {
        val resolved = context.resourceRepository.resolveDimension(component.properties.textSize)
            ?: component.properties.textSize
        val value = DimensionUtils.parseSp(resolved) ?: return null
        val role = rolePredictor.predict(
            textSize = value,
            text = component.properties.text,
            textStyle = component.properties.textStyle
        ) ?: PredictedTextRole.BODY

        return TextSizeEntry(
            component = component,
            role = role,
            value = value
        )
    }

    private fun analyzeRole(entries: List<TextSizeEntry>): List<AnalysisIssue> {
        return NearDuplicateDimensionAnalyzer.analyze(
            entries = entries,
            minDistinctValues = MIN_DISTINCT_VALUES,
            nearDuplicateDistance = NEAR_DUPLICATE_DISTANCE_SP,
            valueSelector = TextSizeEntry::value,
            resultFactory = ::createIssue
        )
    }

    private fun createIssue(entry: TextSizeEntry, canonicalValue: Float): AnalysisIssue {
        return AnalysisIssue(
            ruleId = id,
            severity = Severity.INFO,
            componentId = entry.component.id,
            componentType = entry.component.type,
            filePath = entry.component.filePath,
            message = AnalyzerMessages.xmlTextSizeNearDuplicateCluster(
                value = entry.value,
                canonicalValue = canonicalValue,
                predictedRole = entry.role.name
            ),
            recommendation = AnalyzerMessages.xmlTextSizeNearDuplicateClusterRecommendation(
                canonicalValue = canonicalValue,
                predictedRole = entry.role.name
            )
        )
    }

    private data class TextSizeEntry(
        val component: UiComponent,
        val role: PredictedTextRole,
        val value: Float
    ) {
        val fileRoleKey: String
            get() = "${component.filePath}|${role.name}"
    }

    private companion object {
        const val MIN_DISTINCT_VALUES = 2
        const val NEAR_DUPLICATE_DISTANCE_SP = 1f
    }
}
