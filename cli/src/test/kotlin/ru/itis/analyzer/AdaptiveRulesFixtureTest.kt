package ru.itis.analyzer
import ru.itis.analyzer.messages.rules.RuleIds

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import ru.itis.analyzer.rules.base.Rule
import ru.itis.xml.rules.adaptive.button.XmlAdaptiveButtonStyleOutlierRule
import ru.itis.xml.rules.adaptive.layout.XmlAdaptiveSpacingOutlierRule
import ru.itis.xml.rules.adaptive.text.XmlAdaptiveTextSizeOutlierRule
import ru.itis.xml.rules.adaptive.text.XmlAdaptiveTextStyleOutlierRule
import ru.itis.xml.rules.adaptive.text.XmlTooManyTextStylesOnScreenRule
import ru.itis.xml.rules.static.accessibility.XmlTouchTargetTooSmallRule
import ru.itis.xml.source.importer.XmlProjectImporter
import ru.itis.xml.source.parser.XmlLayoutParser
import ru.itis.xml.source.resource.DefaultResourceRepository

class AdaptiveRulesFixtureTest {

    @Test
    fun `adaptive demo project produces issues for spacing and text size outliers`() {
        val projectRoot = File("src/test/resources/demo-project")
        val importer = XmlProjectImporter()
        val parser = XmlLayoutParser()
        val resourceRepository = DefaultResourceRepository.load(projectRoot)

        val rules: List<Rule> = listOf(
            XmlTouchTargetTooSmallRule(resourceRepository),
            XmlAdaptiveButtonStyleOutlierRule(),
            XmlAdaptiveTextStyleOutlierRule(),
            XmlTooManyTextStylesOnScreenRule(),
            XmlAdaptiveTextSizeOutlierRule(),
            XmlAdaptiveSpacingOutlierRule()
        )

        val analyzer = Analyzer(
            rules = rules,
            resourceRepository = resourceRepository
        )
        val components = importer.findLayoutXmlFiles(projectRoot).map { parser.parse(it) }
        val issues = analyzer.analyze(components)

        assertTrue(
            issues.any { issue -> issue.ruleId == RuleIds.ADAPTIVE_SPACING_OUTLIER },
            "Expected adaptive spacing outlier issue for demo project"
        )

        assertTrue(
            issues.any { issue -> issue.ruleId == RuleIds.ADAPTIVE_TEXT_SIZE_OUTLIER },
            "Expected adaptive text size outlier issue for demo project"
        )

        assertTrue(
            issues.any { issue -> issue.ruleId == RuleIds.ADAPTIVE_TEXT_STYLE_OUTLIER },
            "Expected adaptive text style outlier issue for demo project"
        )

        assertTrue(
            issues.any { issue -> issue.ruleId == RuleIds.ADAPTIVE_BUTTON_STYLE_OUTLIER },
            "Expected adaptive button style outlier issue for demo project"
        )

        assertTrue(
            issues.any { issue ->
                issue.ruleId == RuleIds.ADAPTIVE_TEXT_STYLE_OUTLIER &&
                    issue.componentId == "body_outlier"
            },
            "Expected adaptive text style outlier for predicted body role demo"
        )

        assertTrue(
            issues.any { issue -> issue.ruleId == RuleIds.TOUCH_TARGET_TOO_SMALL },
            "Expected touch target warning for demo project"
        )

        assertTrue(
            issues.any { issue -> issue.ruleId == RuleIds.TOO_MANY_TEXT_STYLES_ON_SCREEN },
            "Expected too many text styles warning for demo project"
        )
    }
}

