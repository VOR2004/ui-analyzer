package ru.itis.compose.rules

import ru.itis.analyzer.rules.base.Rule
import ru.itis.compose.rules.accessibility.ComposeImageContentDescriptionRule
import ru.itis.compose.rules.accessibility.ComposeTouchTargetTooSmallRule
import ru.itis.compose.rules.api.ComposeMissingModifierParameterRule
import ru.itis.compose.rules.color.ComposeButtonColorPerFileConsistencyRule
import ru.itis.compose.rules.color.ComposeHardcodedColorRule
import ru.itis.compose.rules.layout.ComposeAdaptiveSpacingOutlierRule
import ru.itis.compose.rules.layout.ComposeNearDuplicateSpacingClusterRule
import ru.itis.compose.rules.runtime.ComposeRuntimeOffscreenOrClippedComponentRule
import ru.itis.compose.rules.runtime.ComposeRuntimeOverlappingClickableComponentsRule
import ru.itis.compose.rules.runtime.RuntimeDuplicateVisibleTextActionsRule
import ru.itis.compose.rules.runtime.RuntimeScreenDensitySnapshotInfoRule
import ru.itis.compose.rules.runtime.RuntimeSmallTouchTargetRule
import ru.itis.compose.rules.runtime.RuntimeSystemAppSnapshotWarningRule
import ru.itis.compose.rules.runtime.RuntimeTextTruncationRiskRule
import ru.itis.compose.rules.style.ComposeComponentStyleOutlierRule
import ru.itis.compose.rules.text.ComposeAdaptiveTextStyleOutlierRule
import ru.itis.compose.rules.text.ComposeHardcodedTextRule
import ru.itis.compose.rules.text.ComposeTextSizeNearDuplicateClusterRule
import ru.itis.compose.rules.text.ComposeTextContrastRule
import ru.itis.compose.rules.text.ComposeTooManyTextStylesOnScreenRule

object ComposeRuleSet {

    fun default(): List<Rule> {
        return staticRules() + runtimeRules()
    }

    fun staticRules(): List<Rule> {
        return listOf(
            ComposeImageContentDescriptionRule(),
            ComposeTouchTargetTooSmallRule(),
            ComposeHardcodedTextRule(),
            ComposeTextContrastRule(),
            ComposeTooManyTextStylesOnScreenRule(),
            ComposeAdaptiveTextStyleOutlierRule(),
            ComposeTextSizeNearDuplicateClusterRule(),
            ComposeHardcodedColorRule(),
            ComposeButtonColorPerFileConsistencyRule(),
            ComposeComponentStyleOutlierRule(),
            ComposeMissingModifierParameterRule(),
            ComposeAdaptiveSpacingOutlierRule(),
            ComposeNearDuplicateSpacingClusterRule()
        )
    }

    fun runtimeRules(expectedPackageName: String? = null): List<Rule> {
        return listOf(
            RuntimeScreenDensitySnapshotInfoRule(),
            RuntimeSystemAppSnapshotWarningRule(expectedPackageName),
            ComposeRuntimeOverlappingClickableComponentsRule(),
            ComposeRuntimeOffscreenOrClippedComponentRule(),
            RuntimeDuplicateVisibleTextActionsRule(),
            RuntimeSmallTouchTargetRule(),
            RuntimeTextTruncationRiskRule()
        )
    }
}
