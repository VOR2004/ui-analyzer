package ru.itis.analyzer

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.static.structure.DeepLayoutNestingRule
import ru.itis.source.xml.parser.XmlLayoutParser

class DeepLayoutNestingRuleTest {

    @Test
    fun `reports deeply nested layout trees`() {
        val projectRoot = File("src/test/resources/demo-project")
        val layoutFile = File(projectRoot, "app/src/main/res/layout/deep_layout_nesting_demo.xml")
        val component = XmlLayoutParser().parse(layoutFile)

        val issues = DeepLayoutNestingRule().check(listOf(component))

        assertTrue(
            issues.any { issue ->
                issue.ruleId == AnalyzerStrings.RuleIds.DEEP_LAYOUT_NESTING &&
                    issue.componentId == "deep_text"
            },
            "Expected deep layout nesting warning for deepest component"
        )
    }
}
