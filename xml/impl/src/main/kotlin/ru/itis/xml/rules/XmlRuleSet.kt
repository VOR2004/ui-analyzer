package ru.itis.xml.rules

import ru.itis.analyzer.rules.base.Rule
import ru.itis.xml.rules.adaptive.button.XmlAdaptiveButtonStyleOutlierRule
import ru.itis.xml.rules.adaptive.layout.XmlAdaptiveSpacingOutlierRule
import ru.itis.xml.rules.adaptive.layout.XmlNearDuplicateSpacingClusterRule
import ru.itis.xml.rules.adaptive.text.XmlAdaptiveTextSizeOutlierRule
import ru.itis.xml.rules.adaptive.text.XmlAdaptiveTextStyleOutlierRule
import ru.itis.xml.rules.adaptive.text.XmlTextSizeNearDuplicateClusterRule
import ru.itis.xml.rules.adaptive.text.XmlTooManyTextStylesOnScreenRule
import ru.itis.xml.rules.static.accessibility.XmlTouchTargetTooSmallRule
import ru.itis.xml.rules.static.color.XmlButtonColorPerLayoutConsistencyRule
import ru.itis.xml.rules.static.color.XmlButtonColorProjectConsistencyRule
import ru.itis.xml.rules.static.color.XmlHardcodedColorRule
import ru.itis.xml.rules.static.color.XmlNearDuplicateButtonColorRule
import ru.itis.xml.rules.static.common.XmlHardcodedDimensionRule
import ru.itis.xml.rules.static.common.XmlMissingIdRule
import ru.itis.xml.rules.static.image.XmlImageWithoutContentDescriptionRule
import ru.itis.xml.rules.static.structure.XmlDeepLayoutNestingRule
import ru.itis.xml.rules.static.text.XmlHardcodedTextRule
import ru.itis.xml.rules.static.text.XmlSuspiciousTextSizeRule
import ru.itis.xml.rules.static.text.XmlTextContrastRule
import ru.itis.xml.rules.static.text.XmlTextSizeConsistencyRule
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
