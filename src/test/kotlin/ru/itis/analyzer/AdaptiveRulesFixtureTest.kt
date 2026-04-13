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
import ru.itis.analyzer.rules.adaptive.text.TooManyTextStylesOnScreenRule
import ru.itis.analyzer.rules.static.accessibility.TouchTargetTooSmallRule
import ru.itis.importer.ProjectImporter
import ru.itis.parser.XmlLayoutParser

class AdaptiveRulesFixtureTest {

    @Test
    fun `adaptive demo project produces issues for spacing and text size outliers`() {
        val projectRoot = File("src/test/resources/demo-project")
        val importer = ProjectImporter()
        val parser = XmlLayoutParser()
        val resourceRepository = ResourceRepository.load(projectRoot)

        val rules: List<Rule> = listOf(
            TouchTargetTooSmallRule(resourceRepository),
            AdaptiveButtonStyleOutlierRule(),
            AdaptiveTextStyleOutlierRule(),
            TooManyTextStylesOnScreenRule(),
            AdaptiveTextSizeOutlierRule(),
            AdaptiveSpacingOutlierRule()
        )

        val analyzer = Analyzer(
            rules = rules,
            resourceRepository = resourceRepository
        )
        val components = importer.findLayoutXmlFiles(projectRoot).map { parser.parse(it) }
        val issues = analyzer.analyze(components)

        assertTrue(
            issues.any { issue -> issue.ruleId == AnalyzerStrings.RuleIds.ADAPTIVE_SPACING_OUTLIER },
            "Expected adaptive spacing outlier issue for demo project"
        )

        assertTrue(
            issues.any { issue -> issue.ruleId == AnalyzerStrings.RuleIds.ADAPTIVE_TEXT_SIZE_OUTLIER },
            "Expected adaptive text size outlier issue for demo project"
        )

        assertTrue(
            issues.any { issue -> issue.ruleId == AnalyzerStrings.RuleIds.ADAPTIVE_TEXT_STYLE_OUTLIER },
            "Expected adaptive text style outlier issue for demo project"
        )

        assertTrue(
            issues.any { issue -> issue.ruleId == AnalyzerStrings.RuleIds.ADAPTIVE_BUTTON_STYLE_OUTLIER },
            "Expected adaptive button style outlier issue for demo project"
        )

        assertTrue(
            issues.any { issue ->
                issue.ruleId == AnalyzerStrings.RuleIds.ADAPTIVE_TEXT_STYLE_OUTLIER &&
                    issue.componentId == "body_outlier"
            },
            "Expected adaptive text style outlier for predicted body role demo"
        )

        assertTrue(
            issues.any { issue -> issue.ruleId == AnalyzerStrings.RuleIds.TOUCH_TARGET_TOO_SMALL },
            "Expected touch target warning for demo project"
        )

        assertTrue(
            issues.any { issue -> issue.ruleId == AnalyzerStrings.RuleIds.TOO_MANY_TEXT_STYLES_ON_SCREEN },
            "Expected too many text styles warning for demo project"
        )
    }
}
