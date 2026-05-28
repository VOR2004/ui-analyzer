package ru.itis.analyzer.messages.analyzer

object AnalyzerMessages {
    fun tooManyTextStylesOnScreen(
        role: String,
        actualCount: Int,
        dominantSharePercent: Int
    ): String =
        "Predicted text role $role uses too many competing text styles on this screen: $actualCount. " +
            "The dominant style covers only $dominantSharePercent% of elements with this role."

    fun tooManyTextStylesOnScreenRecommendation(role: String): String =
        "Check whether predicted text role $role should use a more consistent typography style."

    fun touchTargetTooSmall(width: String?, height: String?): String =
        "The interactive element is too small for comfortable tapping: width=$width, height=$height."

    const val TOUCH_TARGET_TOO_SMALL_RECOMMENDATION =
        "Interactive elements should generally be at least 48dp wide and 48dp high."

    fun adaptiveButtonStyleOutlier(differences: String): String =
        "The button style differs from the dominant button style on this screen: $differences."

    fun adaptiveButtonStyleOutlierRecommendation(dominantStyle: String): String =
        "Check whether this button should use the dominant screen style: $dominantStyle."

    fun adaptiveSpacingOutlier(propertyName: String, actualValue: Float): String =
        "$propertyName=${actualValue}dp is outside the typical spacing scale used on this screen."

    fun adaptiveSpacingOutlierRecommendation(expected: String): String =
        "Check whether this element should use one of the typical spacing values: $expected."

    fun xmlNearDuplicateSpacingCluster(
        propertyName: String,
        value: Float,
        canonicalValue: Float
    ): String =
        "XML spacing value $propertyName=${value}dp is very close to ${canonicalValue}dp, which is used more often in this layout file."

    fun xmlNearDuplicateSpacingClusterRecommendation(canonicalValue: Float): String =
        "Consider replacing this value with ${canonicalValue}dp or a shared @dimen token to reduce spacing scale fragmentation."

    fun xmlTextSizeNearDuplicateCluster(
        value: Float,
        canonicalValue: Float,
        predictedRole: String
    ): String =
        "XML textSize=${value}sp is very close to ${canonicalValue}sp inside predicted text role $predictedRole."

    fun xmlTextSizeNearDuplicateClusterRecommendation(
        canonicalValue: Float,
        predictedRole: String
    ): String =
        "Consider using ${canonicalValue}sp or a shared @dimen token for predictedRole=$predictedRole to reduce typography scale fragmentation."

    fun adaptiveTextSizeOutlier(actualValue: Float, predictedRole: String? = null): String {
        val roleSuffix = predictedRole?.let { " for predicted text role $it" }.orEmpty()
        return "Text size ${actualValue}sp is outside the typical text sizes$roleSuffix on this screen."
    }

    fun adaptiveTextSizeOutlierRecommendation(expected: String): String =
        "Check whether this element should use one of the typical text sizes: $expected."

    fun adaptiveTextStyleOutlier(differences: String): String =
        "The text style differs from the dominant predicted text role style on this screen: $differences."

    fun adaptiveTextStyleOutlierRecommendation(dominantStyle: String): String =
        "Check whether this element should use the dominant style for its predicted text role: $dominantStyle."

    fun buttonColorPerLayoutNearDuplicate(
        color: String,
        canonicalColor: String,
        distance: String
    ): String =
        "In this layout file, button color $color is very close to $canonicalColor (distance=$distance), which appears more often among similar shades."

    fun buttonColorPerLayoutNearDuplicateRecommendation(canonicalColor: String): String =
        "Use $canonicalColor as the shared shade for visually identical buttons on this screen."

    fun buttonColorPerLayoutNearDominant(
        color: String,
        dominantColor: String,
        distance: String
    ): String =
        "This layout file uses button color $color, which almost matches the primary button color $dominantColor but differs slightly (distance=$distance)."

    fun buttonColorPerLayoutDifferent(color: String, dominantColor: String): String =
        "This layout file uses button color $color, which differs from the most frequent button color $dominantColor."

    const val BUTTON_COLOR_PER_LAYOUT_RECOMMENDATION =
        "Align button colors in this layout file to one consistent style."

    fun buttonColorProjectNearDuplicate(
        color: String,
        canonicalColor: String,
        distance: String
    ): String =
        "Button color $color is very close to $canonicalColor (distance=$distance), which appears more often among similar button colors in the project."

    fun buttonColorProjectNearDuplicateRecommendation(canonicalColor: String): String =
        "Use $canonicalColor as the primary shade for this group of close colors to avoid nearly invisible inconsistencies across the project."

    fun buttonColorProjectNearDominant(
        color: String,
        dominantColor: String,
        distance: String
    ): String =
        "Button color $color almost matches the most common project button color $dominantColor but differs slightly (distance=$distance)."

    fun buttonColorProjectDifferent(color: String, dominantColor: String): String =
        "Button color $color differs from the most common project button color $dominantColor."

    const val BUTTON_COLOR_PROJECT_RECOMMENDATION =
        "Check whether this button matches the global project color style."

    fun hardcodedColor(propertyName: String, color: String): String =
        "Property $propertyName uses a hardcoded color value: $color."

    const val HARDCODED_COLOR_RECOMMENDATION =
        "Move the color to `colors.xml` and reference it as `@color/...`."

    fun nearDuplicateButtonColors(firstColor: String, secondColor: String, distance: Double): String =
        "Button color $firstColor almost matches another button color $secondColor (distance=%.2f).".format(distance)

    const val NEAR_DUPLICATE_BUTTON_COLORS_RECOMMENDATION =
        "Check whether this is an accidental deviation from the shared color style."

    fun hardcodedDimension(propertyName: String, value: String?): String =
        "Property $propertyName uses a hardcoded dimension value: $value."

    const val HARDCODED_DIMENSION_RECOMMENDATION =
        "Move the dimension to `dimens.xml` or use a shared style."

    fun missingId(componentType: String): String =
        "Component $componentType does not define android:id."

    const val MISSING_ID_RECOMMENDATION =
        "Add an id if the element is referenced from code, tests, or should be uniquely identifiable."

    fun imageWithoutContentDescription(componentType: String): String =
        "Image component $componentType does not define android:contentDescription."

    const val IMAGE_WITHOUT_CONTENT_DESCRIPTION_RECOMMENDATION =
        "Add contentDescription for accessibility. If the image is decorative, explicitly use @null."

    fun composeImageContentDescription(componentType: String, isInteractive: Boolean): String {
        val context = if (isInteractive) {
            "inside an interactive Compose container"
        } else {
            "in Compose"
        }
        return "$componentType $context does not provide a meaningful contentDescription."
    }

    const val COMPOSE_IMAGE_CONTENT_DESCRIPTION_RECOMMENDATION =
        "Add contentDescription for meaningful images or icons. For decorative elements, explicitly keep it null; for user-facing text, prefer stringResource(R.string...)."

    fun composeHardcodedText(text: String): String =
        "Compose Text uses a string literal: \"$text\"."

    const val COMPOSE_HARDCODED_TEXT_RECOMMENDATION =
        "If this is user-facing text, move it to string resources and use stringResource(R.string...). Dynamic values and technical labels do not need to be extracted."

    fun composeHardcodedColor(propertyName: String, value: String): String =
        "Compose property $propertyName uses an inline color value: $value."

    const val COMPOSE_HARDCODED_COLOR_RECOMMENDATION =
        "Use MaterialTheme.colorScheme, a Compose theme token, or colorResource(R.color...) instead of inline colors when the color belongs to the UI style."

    fun composeMissingModifierParameter(functionName: String, predictedRole: String): String =
        "Composable function $functionName does not accept a modifier parameter. predictedRole=$predictedRole."

    const val COMPOSE_MISSING_MODIFIER_PARAMETER_RECOMMENDATION =
        "If this function is a reusable UI component, add a modifier parameter: Modifier = Modifier and apply it to the root element."

    fun composeAdaptiveSpacingOutlier(propertyName: String, actualValue: Float): String =
        "Compose value $propertyName=${actualValue}dp is outside the local spacing scale in this file."

    fun composeAdaptiveSpacingOutlierRecommendation(expected: String): String =
        "Check whether this Compose element should use one of the typical spacing values: $expected."

    fun composeTooManyTextStylesOnScreen(
        actualCount: Int,
        dominantSharePercent: Int
    ): String =
        "This Compose file uses too many competing text styles: $actualCount. The dominant style covers only $dominantSharePercent% of Text components."

    const val COMPOSE_TOO_MANY_TEXT_STYLES_ON_SCREEN_RECOMMENDATION =
        "Check whether Text components can be aligned to a shared typography system such as MaterialTheme.typography, shared style parameters, or reusable text components."

    fun composeAdaptiveTextStyleOutlier(differences: String, predictedRole: String): String =
        "Compose Text differs from the dominant style inside predicted text role $predictedRole: $differences."

    fun composeAdaptiveTextStyleOutlierRecommendation(dominantStyle: String): String =
        "Check whether this Text should use the dominant Compose style: $dominantStyle."

    fun composeInsufficientTextContrast(
        textColor: String,
        backgroundColor: String,
        ratio: String,
        minContrast: Double
    ): String =
        "Insufficient contrast between Compose Text ($textColor) and the nearest background ($backgroundColor): ratio=$ratio (required >= $minContrast)."

    const val COMPOSE_INSUFFICIENT_TEXT_CONTRAST_RECOMMENDATION =
        "Increase contrast between Compose Text and its background, or use appropriate MaterialTheme.colorScheme tokens."

    fun composeButtonColorPerFileNearDuplicate(
        color: String,
        canonicalColor: String,
        distance: String
    ): String =
        "In this Compose file, button color $color is very close to $canonicalColor (distance=$distance), which appears more often among similar button colors."

    fun composeButtonColorPerFileNearDuplicateRecommendation(canonicalColor: String): String =
        "Use $canonicalColor as the shared shade for visually identical Compose buttons in this file."

    fun composeButtonColorPerFileNearDominant(
        color: String,
        dominantColor: String,
        distance: String
    ): String =
        "This Compose file uses button color $color, which almost matches the primary button color $dominantColor but differs slightly (distance=$distance)."

    fun composeButtonColorPerFileDifferent(color: String, dominantColor: String): String =
        "This Compose file uses button color $color, which differs from the most frequent button color $dominantColor."

    const val COMPOSE_BUTTON_COLOR_PER_FILE_RECOMMENDATION =
        "Check whether this Compose button matches the local file color system: MaterialTheme.colorScheme or a shared buttonColors token."

    fun composeComponentStyleOutlier(componentType: String, differences: String): String =
        "Compose component $componentType differs from the dominant local style of similar components: $differences."

    fun composeComponentStyleOutlierRecommendation(dominantStyle: String): String =
        "Check whether this component should use the dominant Compose style for similar components in this file: $dominantStyle."

    fun composeTouchTargetTooSmall(width: String?, height: String?): String =
        "The interactive Compose element is too small for comfortable tapping: width=$width, height=$height."

    const val COMPOSE_TOUCH_TARGET_TOO_SMALL_RECOMMENDATION =
        "Interactive Compose elements should generally be at least 48.dp wide and 48.dp high, or provide a minimum tap area via Modifier.sizeIn/minimumInteractiveComponentSize."

    fun composeRuntimeOverlappingClickableComponents(
        firstComponent: String,
        secondComponent: String,
        overlapArea: String
    ): String =
        "Runtime Compose clickable components overlap: $firstComponent and $secondComponent, overlapArea=$overlapArea."

    const val COMPOSE_RUNTIME_OVERLAPPING_CLICKABLE_COMPONENTS_RECOMMENDATION =
        "Check the actual runtime bounds: overlapping clickable areas can cause ambiguous taps and accessibility issues."

    fun composeRuntimeOffscreenOrClippedComponent(
        component: String,
        bounds: String,
        screenBounds: String,
        reason: String
    ): String =
        "Runtime component $component has suspicious bounds: bounds=$bounds, screenBounds=$screenBounds, reason=$reason."

    const val COMPOSE_RUNTIME_OFFSCREEN_OR_CLIPPED_COMPONENT_RECOMMENDATION =
        "Check the actual runtime layout: the component may be invisible, clipped, placed outside the screen, or measured with an invalid size."

    fun runtimeSystemAppSnapshotWarning(
        expectedPackage: String,
        actualPackage: String
    ): String =
        "Runtime snapshot was captured from package $actualPackage, but the expected project package is $expectedPackage."

    const val RUNTIME_SYSTEM_APP_SNAPSHOT_WARNING_RECOMMENDATION =
        "Before runtime analysis, open the project app on the emulator/device and capture the snapshot again. Example: adb shell monkey -p <applicationId> 1."

    fun runtimeDuplicateVisibleTextActions(
        label: String,
        count: Int,
        examples: String
    ): String =
        "Runtime screen contains $count clickable elements with the same visible label \"$label\": $examples."

    const val RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS_RECOMMENDATION =
        "Check whether these actions are distinguishable enough for users, accessibility, and UI tests. If the elements repeat in a list, add a more specific contentDescription or another accessible context."

    fun runtimeSmallTouchTarget(component: String, width: String, height: String): String =
        "Runtime clickable component $component has a small actual touch target: width=$width, height=$height."

    const val RUNTIME_SMALL_TOUCH_TARGET_RECOMMENDATION =
        "Check the rendered bounds on the device. Interactive elements should usually have at least a 48dp touch target; runtime bounds are reported in snapshot pixels, so this rule is a conservative signal."

    fun runtimeTextTruncationRisk(component: String, text: String, bounds: String, estimatedWidth: String): String =
        "Runtime text component $component may be visually truncated: text=\"$text\", bounds=$bounds, estimatedTextWidth=$estimatedWidth."

    const val RUNTIME_TEXT_TRUNCATION_RISK_RECOMMENDATION =
        "Check the screen with the current device, font scale and locale. Consider allowing more width, wrapping text, shortening the label, or using responsive layout constraints."

    fun runtimeScreenDensitySnapshotInfo(
        source: String,
        screen: String,
        state: String,
        size: String,
        orientation: String,
        density: String
    ): String =
        "Runtime snapshot context: source=$source, screen=$screen, state=$state, size=$size, orientation=$orientation, density=$density."

    const val RUNTIME_SCREEN_DENSITY_SNAPSHOT_INFO_RECOMMENDATION =
        "Use this entry as runtime context for bounds-based findings. If density/orientation is unknown, include it in the runtime snapshot metadata or verify it on the emulator/device."

    fun composeNearDuplicateSpacingCluster(
        propertyName: String,
        value: Float,
        canonicalValue: Float
    ): String =
        "Compose spacing value $propertyName=${value}dp is very close to ${canonicalValue}dp, which is used more often in this file."

    fun composeNearDuplicateSpacingClusterRecommendation(canonicalValue: Float): String =
        "Consider replacing this value with ${canonicalValue}dp to reduce spacing scale fragmentation."

    fun composeTextSizeNearDuplicateCluster(
        value: Float,
        canonicalValue: Float,
        predictedRole: String
    ): String =
        "Compose textSize=${value}sp is very close to ${canonicalValue}sp inside predicted text role $predictedRole."

    fun composeTextSizeNearDuplicateClusterRecommendation(
        canonicalValue: Float,
        predictedRole: String
    ): String =
        "Consider using ${canonicalValue}sp for predictedRole=$predictedRole to reduce typography scale fragmentation."

    fun hardcodedText(componentType: String, text: String): String =
        "Component $componentType uses hardcoded text: \"$text\"."

    const val HARDCODED_TEXT_RECOMMENDATION =
        "Move the text to `strings.xml` and reference it as `@string/...`."

    fun suspiciousTextSizeTooSmall(rawTextSize: String): String =
        "Suspiciously small text size: $rawTextSize."

    const val SUSPICIOUS_TEXT_SIZE_TOO_SMALL_RECOMMENDATION =
        "Check android:textSize: this may be a layout mistake."

    fun suspiciousTextSizeTooLarge(rawTextSize: String): String =
        "Suspiciously large text size: $rawTextSize."

    const val SUSPICIOUS_TEXT_SIZE_TOO_LARGE_RECOMMENDATION =
        "Check whether this text size fits the element's purpose."

    fun insufficientTextContrast(
        textColor: String,
        backgroundColor: String,
        ratio: String,
        minContrast: Double
    ): String =
        "Insufficient contrast between text ($textColor) and background ($backgroundColor): ratio=$ratio (required >= $minContrast)."

    const val INSUFFICIENT_TEXT_CONTRAST_RECOMMENDATION =
        "Increase contrast between text and background for better readability."

    fun textSizeConsistency(size: String, dominantSize: String?): String =
        "Text size $size differs from the most frequently used size $dominantSize."

    fun deepLayoutNesting(depth: Int): String =
        "Layout tree nesting is too deep: $depth levels."

    const val DEEP_LAYOUT_NESTING_RECOMMENDATION =
        "Check the layout structure: deep nesting should be simplified with ConstraintLayout, include/merge, or a flatter composition."

    const val TEXT_SIZE_CONSISTENCY_RECOMMENDATION =
        "Check whether this text should use the shared typography style."
}
