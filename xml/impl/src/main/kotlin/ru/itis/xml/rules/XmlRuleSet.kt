package ru.itis.xml.rules

import ru.itis.analyzer.rules.base.Rule
import ru.itis.xml.rules.adaptive.button.XmlAdaptiveButtonStyleOutlierRule
import ru.itis.xml.rules.adaptive.layout.XmlAdaptiveSpacingOutlierRule
import ru.itis.xml.rules.adaptive.layout.XmlNearDuplicateSpacingClusterRule
import ru.itis.xml.rules.adaptive.text.XmlAdaptiveTextSizeOutlierRule
import ru.itis.xml.rules.adaptive.text.XmlAdaptiveTextStyleOutlierRule
import ru.itis.xml.rules.adaptive.text.XmlTextSizeNearDuplicateClusterRule
import ru.itis.xml.rules.adaptive.text.XmlTooManyTextStylesOnScreenRule
import ru.itis.xml.rules.baseline.accessibility.XmlTouchTargetTooSmallRule
import ru.itis.xml.rules.baseline.color.XmlButtonColorPerLayoutConsistencyRule
import ru.itis.xml.rules.baseline.color.XmlButtonColorProjectConsistencyRule
import ru.itis.xml.rules.baseline.color.XmlHardcodedColorRule
import ru.itis.xml.rules.baseline.color.XmlNearDuplicateButtonColorRule
import ru.itis.xml.rules.baseline.common.XmlHardcodedDimensionRule
import ru.itis.xml.rules.baseline.common.XmlMissingIdRule
import ru.itis.xml.rules.baseline.image.XmlImageWithoutContentDescriptionRule
import ru.itis.xml.rules.baseline.structure.XmlDeepLayoutNestingRule
import ru.itis.xml.rules.baseline.text.XmlHardcodedTextRule
import ru.itis.xml.rules.baseline.text.XmlSuspiciousTextSizeRule
import ru.itis.xml.rules.baseline.text.XmlTextContrastRule
import ru.itis.xml.rules.baseline.text.XmlTextSizeConsistencyRule
import ru.itis.xml.source.resource.ResourceRepository

object XmlRuleSet {

    fun default(resourceRepository: ResourceRepository): List<Rule> {
        return listOf(
            XmlSuspiciousTextSizeRule(resourceRepository),
            XmlButtonColorPerLayoutConsistencyRule(resourceRepository),
            XmlButtonColorProjectConsistencyRule(resourceRepository),
            XmlTextContrastRule(resourceRepository),
            XmlTextSizeConsistencyRule(resourceRepository),
            XmlHardcodedColorRule(),
            XmlNearDuplicateButtonColorRule(resourceRepository),
            XmlMissingIdRule(),
            XmlHardcodedDimensionRule(),
            XmlImageWithoutContentDescriptionRule(),
            XmlDeepLayoutNestingRule(),
            XmlHardcodedTextRule(),
            XmlTouchTargetTooSmallRule(resourceRepository),
            XmlAdaptiveTextStyleOutlierRule(),
            XmlTooManyTextStylesOnScreenRule(),
            XmlAdaptiveTextSizeOutlierRule(),
            XmlTextSizeNearDuplicateClusterRule(),
            XmlAdaptiveSpacingOutlierRule(),
            XmlNearDuplicateSpacingClusterRule(),
            XmlAdaptiveButtonStyleOutlierRule()
        )
    }
}
