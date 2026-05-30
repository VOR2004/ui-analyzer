package ru.itis.report.markdown.config

import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity

internal const val MAX_TABLE_ROWS = 10
internal const val NOT_AVAILABLE = "_not available_"
internal const val UNKNOWN_VALUE = "unknown"

internal const val LOCATOR_DETAIL_SEPARATOR = ","
internal const val COMPOSE_FUNCTION_LOCATOR_KEY = "composable"
internal const val VISUAL_SOURCE_LOCATOR_KEY = "visualSource"
internal const val IMAGE_VECTOR_LOCATOR_KEY = "imageVector"
internal const val PAINTER_LOCATOR_KEY = "painter"
internal const val PATH_LOCATOR_KEY = "path"

internal const val COMPOSE_RULE_PREFIX = "compose-"
internal const val COMPOSE_RUNTIME_RULE_PREFIX = "compose-runtime-"
internal const val XML_RULE_PREFIX = "xml-"
internal const val RUNTIME_RULE_PREFIX = "runtime-"

internal const val XML_FILE_EXTENSION = ".xml"
internal const val KOTLIN_FILE_EXTENSION = ".kt"

internal const val MAX_RUNTIME_PATH_SEGMENTS = 4
internal const val PATH_SEPARATOR = '/'
internal const val PATH_ELLIPSIS = "..."
internal const val TREE_BRANCH = "-"
internal const val TREE_INDENT = "  "

internal const val COMPOSE_RUNTIME_OVERLAPPING_CLICKABLE_COMPONENTS_RULE_ID =
    "compose-runtime-overlapping-clickable-components"
internal const val RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS_RULE_ID =
    "runtime-duplicate-visible-text-actions"
internal const val RUNTIME_SMALL_TOUCH_TARGET_RULE_ID = "runtime-small-touch-target"
internal const val RUNTIME_TEXT_TRUNCATION_RISK_RULE_ID = "runtime-text-truncation-risk"

internal const val ID_MARKER = "id="
internal const val BOUNDS_MARKER = "bounds="

internal val ANCHOR_DASH_SEQUENCE_PATTERN = Regex("-+")
internal val OVERLAP_AREA_PATTERN = Regex("""overlapArea=([^.\s]+)""")
internal val FIRST_NUMBER_PATTERN = Regex("""\b(\d+)\b""")
internal val DUPLICATE_ACTIONS_LABEL_PATTERN = Regex(""""([^"]+)"""")
internal val ID_PATTERN = Regex("""\bid=([^,\]]+)""")
internal val BOUNDS_PATTERN = Regex("""bounds=\[([^]]+)]""")
internal val WIDTH_PATTERN = Regex("""width=([^,.\s]+)""")
internal val HEIGHT_PATTERN = Regex("""height=([^,.\s]+)""")
internal val TEXT_PATTERN = Regex("""text="([^"]*)"""")
internal val MESSAGE_BOUNDS_PATTERN = Regex("""bounds=(.+?),\s+estimatedTextWidth=""")
internal val ESTIMATED_TEXT_WIDTH_PATTERN = Regex("""estimatedTextWidth=([^.\s]+)""")

private val severityRank = mapOf(
    Severity.ERROR to 0,
    Severity.WARNING to 1,
    Severity.INFO to 2
)

internal val issueComparator = compareBy<AnalysisIssue> { issue -> severityRank.getValue(issue.severity) }
    .thenBy { issue -> issue.filePath }
    .thenBy { issue -> issue.ruleId }
    .thenBy { issue -> issue.componentId.orEmpty() }
