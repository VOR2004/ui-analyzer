package ru.itis.analyzer

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.base.Rule
import ru.itis.analyzer.rules.static.accessibility.TouchTargetTooSmallRule
import ru.itis.analyzer.rules.static.color.NearDuplicateButtonColorRule
import ru.itis.analyzer.rules.static.text.HardcodedTextRule
import ru.itis.analyzer.rules.static.text.SuspiciousTextSizeRule
import ru.itis.analyzer.rules.static.text.TextContrastRule
import ru.itis.source.xml.parser.XmlLayoutParser
import ru.itis.source.xml.resource.ResourceRepository

class ResourceAwareRulesTest {

    @Test
    fun `rules resolve values resources before analysis`() {
        val projectRoot = File("src/test/resources/demo-project")
        val layoutFile = File(
            projectRoot,
            "app/src/main/res/layout/rule_resource_resolution_demo.xml"
        )
        val resourceRepository = ResourceRepository.load(projectRoot)
        val component = XmlLayoutParser().parse(layoutFile)

        val rules: List<Rule> = listOf(
            TouchTargetTooSmallRule(resourceRepository),
            SuspiciousTextSizeRule(resourceRepository),
            TextContrastRule(resourceRepository),
            HardcodedTextRule(),
            NearDuplicateButtonColorRule(resourceRepository)
        )

        val issues = Analyzer(
            rules = rules,
            resourceRepository = resourceRepository
        ).analyze(listOf(component))

        assertTrue(
            issues.any {
                it.ruleId == AnalyzerStrings.RuleIds.TOUCH_TARGET_TOO_SMALL &&
                    it.componentId == "resource_tiny_button"
            },
            "Expected touch target rule to resolve width and height from theme dimen attrs"
        )

        assertTrue(
            issues.any {
                it.ruleId == AnalyzerStrings.RuleIds.SUSPICIOUS_TEXT_SIZE &&
                    it.componentId == "resource_tiny_text"
            },
            "Expected suspicious text size rule to resolve textSize from theme dimen attr"
        )

        assertTrue(
            issues.any {
                it.ruleId == AnalyzerStrings.RuleIds.TEXT_CONTRAST &&
                    it.componentId == "resource_low_contrast_text"
            },
            "Expected text contrast rule to resolve text color and background from theme attrs"
        )

        assertTrue(
            issues.any {
                it.ruleId == AnalyzerStrings.RuleIds.HARDCODED_TEXT &&
                    it.componentId == "raw_hardcoded_text"
            },
            "Expected raw literal text to be reported as hardcoded"
        )

        assertFalse(
            issues.any {
                it.ruleId == AnalyzerStrings.RuleIds.HARDCODED_TEXT &&
                    it.componentId == "resource_safe_text"
            },
            "Expected @string text not to be reported as hardcoded"
        )

        assertTrue(
            issues.any { it.ruleId == AnalyzerStrings.RuleIds.NEAR_DUPLICATE_BUTTON_COLORS },
            "Expected near duplicate button colors to resolve @color resources"
        )
    }
}
