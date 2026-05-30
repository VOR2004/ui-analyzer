package ru.itis.analyzer
import ru.itis.analyzer.messages.rules.RuleIds

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import ru.itis.xml.rules.baseline.structure.XmlDeepLayoutNestingRule
import ru.itis.xml.source.parser.XmlLayoutParser

class XmlDeepLayoutNestingRuleTest {

    @Test
    fun `reports deeply nested layout trees`() {
        val projectRoot = File("src/test/resources/demo-project")
        val layoutFile = File(projectRoot, "app/src/main/res/layout/deep_layout_nesting_demo.xml")
        val component = XmlLayoutParser().parse(layoutFile)

        val issues = XmlDeepLayoutNestingRule().check(listOf(component))

        assertTrue(
            issues.any { issue ->
                issue.ruleId == RuleIds.DEEP_LAYOUT_NESTING &&
                    issue.componentId == "deep_text"
            },
            "Expected deep layout nesting warning for deepest component"
        )
    }
}


