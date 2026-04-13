package ru.itis.analyzer

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.resource.ResourceRepository
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.rules.adaptive.button.AdaptiveButtonStyleOutlierRule
import ru.itis.analyzer.rules.adaptive.layout.AdaptiveSpacingOutlierRule
import ru.itis.analyzer.rules.adaptive.text.AdaptiveTextSizeOutlierRule
import ru.itis.analyzer.rules.adaptive.text.AdaptiveTextStyleOutlierRule
import ru.itis.parser.XmlLayoutParser

class AdaptiveResourceAwareRulesTest {

    @Test
    fun `adaptive rules resolve resources before comparing styles`() {
        val projectRoot = File("src/test/resources/demo-project")
        val layoutFile = File(
            projectRoot,
            "app/src/main/res/layout/adaptive_resource_resolution_demo.xml"
        )
        val resourceRepository = ResourceRepository.load(projectRoot)
        val component = XmlLayoutParser().parse(layoutFile)

        val rules: List<Rule> = listOf(
            AdaptiveButtonStyleOutlierRule(),
            AdaptiveTextStyleOutlierRule(),
            AdaptiveTextSizeOutlierRule(),
            AdaptiveSpacingOutlierRule()
        )

        val issues = Analyzer(
            rules = rules,
            resourceRepository = resourceRepository
        ).analyze(listOf(component))

        assertTrue(
            issues.any {
                it.ruleId == AnalyzerStrings.RuleIds.ADAPTIVE_SPACING_OUTLIER &&
                    it.componentId == "adaptive_resource_spacing_outlier"
            },
            "Expected adaptive spacing rule to resolve @dimen and ?attr spacing values"
        )

        assertTrue(
            issues.any {
                it.ruleId == AnalyzerStrings.RuleIds.ADAPTIVE_TEXT_SIZE_OUTLIER &&
                    it.componentId == "adaptive_resource_text_size_outlier"
            },
            "Expected adaptive text size rule to resolve @dimen and ?attr text sizes"
        )

        assertTrue(
            issues.any {
                it.ruleId == AnalyzerStrings.RuleIds.ADAPTIVE_TEXT_STYLE_OUTLIER &&
                    it.componentId == "adaptive_resource_text_style_outlier"
            },
            "Expected adaptive text style rule to resolve text resources and dimensions"
        )

        assertTrue(
            issues.any {
                it.ruleId == AnalyzerStrings.RuleIds.ADAPTIVE_BUTTON_STYLE_OUTLIER &&
                    it.componentId == "adaptive_resource_button_outlier"
            },
            "Expected adaptive button style rule to resolve @color, ?attr and @dimen resources"
        )
    }
}
