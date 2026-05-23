package ru.itis.analyzer.messages.rules

object RuleIds {
    const val ADAPTIVE_BUTTON_STYLE_OUTLIER = "adaptive-button-style-outlier"
    const val ADAPTIVE_SPACING_OUTLIER = "adaptive-spacing-outlier"
    const val ADAPTIVE_TEXT_STYLE_OUTLIER = "adaptive-text-style-outlier"
    const val ADAPTIVE_TEXT_SIZE_OUTLIER = "adaptive-text-size-outlier"
    const val TOO_MANY_TEXT_STYLES_ON_SCREEN = "too-many-text-styles-on-screen"
    const val BUTTON_COLOR_PER_LAYOUT_CONSISTENCY = "button-color-per-layout-consistency"
    const val BUTTON_COLOR_PROJECT_CONSISTENCY = "button-color-project-consistency"
    const val HARDCODED_COLOR = "hardcoded-color"
    const val NEAR_DUPLICATE_BUTTON_COLORS = "near-duplicate-button-colors"
    const val HARDCODED_DIMENSION = "hardcoded-dimension"
    const val MISSING_ID = "missing-id"
    const val IMAGE_WITHOUT_CONTENT_DESCRIPTION = "image-without-content-description"
    const val HARDCODED_TEXT = "hardcoded-text"
    const val SUSPICIOUS_TEXT_SIZE = "suspicious-text-size"
    const val TEXT_CONTRAST = "text-contrast"
    const val TEXT_SIZE_CONSISTENCY = "text-size-consistency"
    const val TOUCH_TARGET_TOO_SMALL = "touch-target-too-small"
    const val DEEP_LAYOUT_NESTING = "deep-layout-nesting"
    const val XML_NEAR_DUPLICATE_SPACING_CLUSTER = "xml-near-duplicate-spacing-cluster"
    const val XML_TEXT_SIZE_NEAR_DUPLICATE_CLUSTER = "xml-text-size-near-duplicate-cluster"
    const val COMPOSE_IMAGE_CONTENT_DESCRIPTION = "compose-image-content-description"
    const val COMPOSE_HARDCODED_TEXT = "compose-hardcoded-text"
    const val COMPOSE_HARDCODED_COLOR = "compose-hardcoded-color"
    const val COMPOSE_MISSING_MODIFIER_PARAMETER = "compose-missing-modifier-parameter"
    const val COMPOSE_ADAPTIVE_SPACING_OUTLIER = "compose-adaptive-spacing-outlier"
    const val COMPOSE_TOO_MANY_TEXT_STYLES_ON_SCREEN = "compose-too-many-text-styles-on-screen"
    const val COMPOSE_ADAPTIVE_TEXT_STYLE_OUTLIER = "compose-adaptive-text-style-outlier"
    const val COMPOSE_BUTTON_COLOR_PER_FILE_CONSISTENCY = "compose-button-color-per-file-consistency"
    const val COMPOSE_TOUCH_TARGET_TOO_SMALL = "compose-touch-target-too-small"
    const val COMPOSE_NEAR_DUPLICATE_SPACING_CLUSTER = "compose-near-duplicate-spacing-cluster"
    const val COMPOSE_TEXT_SIZE_NEAR_DUPLICATE_CLUSTER = "compose-text-size-near-duplicate-cluster"
    const val COMPOSE_COMPONENT_STYLE_OUTLIER = "compose-component-style-outlier"
    const val COMPOSE_TEXT_CONTRAST = "compose-text-contrast"
    const val COMPOSE_RUNTIME_OVERLAPPING_CLICKABLE_COMPONENTS =
        "compose-runtime-overlapping-clickable-components"
    const val COMPOSE_RUNTIME_OFFSCREEN_OR_CLIPPED_COMPONENT =
        "compose-runtime-offscreen-or-clipped-component"
    const val RUNTIME_SYSTEM_APP_SNAPSHOT_WARNING =
        "runtime-system-app-snapshot-warning"
    const val RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS =
        "runtime-duplicate-visible-text-actions"

    fun nearDuplicateCluster(baseId: String): String = "$baseId-near-duplicate-cluster"
}