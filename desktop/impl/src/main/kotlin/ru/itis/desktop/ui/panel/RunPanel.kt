package ru.itis.desktop.ui.panel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import ru.itis.desktop.analysis.DesktopAnalysisResult
import ru.itis.desktop.text.DesktopIssueReportText
import ru.itis.desktop.text.DesktopPanelText
import ru.itis.desktop.ui.component.FilterIcon
import ru.itis.desktop.ui.component.IconOnlyButton
import ru.itis.desktop.ui.component.Panel
import ru.itis.desktop.ui.component.RulesIcon
import ru.itis.model.AnalysisIssue
import ru.itis.model.Severity

@Composable
fun RunPanel(
    status: String,
    isRunning: Boolean,
    rulesPanelVisible: Boolean,
    selectedRuleCount: Int,
    totalRuleCount: Int,
    analysisResult: DesktopAnalysisResult?,
    onToggleRulesPanel: () -> Unit,
    onRun: () -> Unit
) {
    Panel(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = DesktopPanelText.ANALYSIS_WORKSPACE,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = DesktopPanelText.selectedRules(selectedRuleCount, totalRuleCount),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconOnlyButton(
                    onClick = onToggleRulesPanel,
                    selected = rulesPanelVisible
                ) { color -> RulesIcon(color = color) }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = DesktopPanelText.STATUS,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = status,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (isRunning) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 14.dp))
                }
                Button(
                    enabled = !isRunning,
                    onClick = onRun
                ) {
                    Text(
                        text = DesktopPanelText.RUN_ANALYSIS,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            IssueReportPanel(
                result = analysisResult,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun IssueReportPanel(
    result: DesktopAnalysisResult?,
    modifier: Modifier = Modifier
) {
    if (result == null) {
        EmptyIssueReport(modifier)
        return
    }

    var sourceFilter by remember(result) { mutableStateOf(IssueSourceFilter.ALL) }
    var severityFilter by remember(result) { mutableStateOf<Severity?>(null) }
    var ruleFilter by remember(result) { mutableStateOf<String?>(null) }
    var selectedIssueIndex by remember(result) { mutableStateOf(0) }
    var filtersVisible by remember(result) { mutableStateOf(true) }
    val severityFilters = remember { listOf(null) + Severity.entries.toList() }

    val availableRules = remember(result.issues) {
        result.issues.map { issue -> issue.ruleId }.distinct().sorted()
    }
    val filteredIssues = result.issues
        .filter { issue -> sourceFilter == IssueSourceFilter.ALL || issue.sourceFilter() == sourceFilter }
        .filter { issue -> severityFilter == null || issue.severity == severityFilter }
        .filter { issue -> ruleFilter == null || issue.ruleId == ruleFilter }
    val selectedIssue = filteredIssues.getOrNull(selectedIssueIndex)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = DesktopIssueReportText.ISSUES,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = DesktopIssueReportText.showingIssues(filteredIssues.size, result.issueCount),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconOnlyButton(
                onClick = { filtersVisible = !filtersVisible },
                selected = filtersVisible
            ) { color -> FilterIcon(color = color) }
        }

        if (filtersVisible) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IssueSourceFilter.entries.forEach { filter ->
                    FilterChip(
                        text = filter.label,
                        selected = sourceFilter == filter,
                        onClick = {
                            sourceFilter = filter
                            selectedIssueIndex = 0
                        }
                    )
                }
                severityFilters.forEach { filter ->
                    FilterChip(
                        text = filter?.name ?: DesktopIssueReportText.ALL_SEVERITIES,
                        selected = severityFilter == filter,
                        onClick = {
                            severityFilter = filter
                            selectedIssueIndex = 0
                        }
                    )
                }
            }

            RuleFilterRow(
                rules = availableRules,
                selectedRule = ruleFilter,
                onRuleSelected = { value ->
                    ruleFilter = value
                    selectedIssueIndex = 0
                }
            )
        } else if (sourceFilter != IssueSourceFilter.ALL || severityFilter != null || ruleFilter != null) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    text = activeFilterSummary(sourceFilter, severityFilter, ruleFilter),
                    selected = true,
                    onClick = { filtersVisible = true }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 260.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IssueList(
                issues = filteredIssues,
                selectedIssueIndex = selectedIssueIndex,
                onIssueSelected = { index -> selectedIssueIndex = index },
                modifier = Modifier.weight(0.95f)
            )
            IssueDetails(
                issue = selectedIssue,
                modifier = Modifier.weight(1.25f)
            )
        }
    }
}

@Composable
private fun EmptyIssueReport(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = DesktopIssueReportText.ISSUES, fontWeight = FontWeight.SemiBold)
            Text(
                text = DesktopIssueReportText.EMPTY_REPORT,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun RuleFilterRow(
    rules: List<String>,
    selectedRule: String?,
    onRuleSelected: (String?) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            text = DesktopIssueReportText.ALL_RULES,
            selected = selectedRule == null,
            onClick = { onRuleSelected(null) }
        )
        rules.forEach { rule ->
            FilterChip(
                text = rule,
                selected = selectedRule == rule,
                onClick = { onRuleSelected(rule) }
            )
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun IssueList(
    issues: List<AnalysisIssue>,
    selectedIssueIndex: Int,
    onIssueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.heightIn(max = 420.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(start = 8.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (issues.isEmpty()) {
                    Text(
                        text = DesktopIssueReportText.EMPTY_FILTER_RESULT,
                        modifier = Modifier.padding(6.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
                issues.forEachIndexed { index, issue ->
                    IssueRow(
                        issue = issue,
                        selected = index == selectedIssueIndex,
                        onClick = { onIssueSelected(index) }
                    )
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun IssueRow(
    issue: AnalysisIssue,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SeverityBadge(issue.severity)
                Text(
                    text = issue.ruleId,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = issue.subtitle(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun IssueDetails(
    issue: AnalysisIssue?,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.heightIn(min = 260.dp, max = 420.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        if (issue == null) {
            Text(
                text = DesktopIssueReportText.SELECT_ISSUE,
                modifier = Modifier.padding(14.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Surface
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .padding(end = 10.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    SeverityBadge(issue.severity)
                    Text(text = issue.ruleId, fontWeight = FontWeight.Bold)
                }
                DetailLine(DesktopIssueReportText.SOURCE, issue.sourceFilter().label)
                DetailLine(DesktopIssueReportText.COMPONENT, issue.componentType)
                DetailLine(DesktopIssueReportText.COMPONENT_ID, issue.componentId ?: DesktopIssueReportText.NOT_AVAILABLE)
                issue.componentLocator?.extractLocatorDetail(COMPOSE_FUNCTION_LOCATOR_KEY)?.let { composable ->
                    DetailLine(DesktopIssueReportText.COMPOSABLE, composable)
                }
                issue.componentLocator?.extractVisualSource()?.let { visualSource ->
                    DetailLine(DesktopIssueReportText.VISUAL_SOURCE, visualSource)
                }
                issue.componentLocator?.let { locator ->
                    DetailLine(DesktopIssueReportText.LOCATOR, locator.shortenMiddle())
                }
                FileDetailLine(path = issue.filePath)
                issue.message?.takeIf { value -> value.isNotBlank() }?.let { value -> DetailLine(DesktopIssueReportText.MESSAGE, value) }
                DetailLine(DesktopIssueReportText.RECOMMENDATION, issue.recommendation)
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            maxLines = 1,
            softWrap = false
        )
        Text(
            text = value,
            fontSize = 13.sp,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FileDetailLine(path: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = DesktopIssueReportText.FILE,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            maxLines = 1,
            softWrap = false
        )
        Text(
            text = path,
            modifier = Modifier.clickable { revealFileInExplorer(path) },
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SeverityBadge(severity: Severity) {
    val color = when (severity) {
        Severity.ERROR -> Color(0xFFE06C75)
        Severity.WARNING -> Color(0xFFE5C07B)
        Severity.INFO -> MaterialTheme.colorScheme.primary
    }
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = color.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.75f))
    ) {
        Text(
            text = DesktopIssueReportText.severity(severity),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false
        )
    }
}

private fun AnalysisIssue.sourceFilter(): IssueSourceFilter {
    return when {
        ruleId.startsWith("compose-runtime-") -> IssueSourceFilter.RUNTIME
        ruleId.startsWith("runtime-") -> IssueSourceFilter.RUNTIME
        ruleId.startsWith("compose-") -> IssueSourceFilter.COMPOSE
        ruleId.startsWith("xml-") -> IssueSourceFilter.XML
        filePath.endsWith(".kt", ignoreCase = true) -> IssueSourceFilter.COMPOSE
        filePath.endsWith(".xml", ignoreCase = true) -> IssueSourceFilter.XML
        else -> IssueSourceFilter.PROJECT
    }
}

private fun String.shortenMiddle(maxLength: Int = 140): String {
    if (length <= maxLength) return this
    val edge = (maxLength - 3) / 2
    return take(edge) + "..." + takeLast(edge)
}

private fun AnalysisIssue.subtitle(): String {
    return componentId
        ?: componentLocator?.extractLocatorDetail(COMPOSE_FUNCTION_LOCATOR_KEY)
        ?: componentLocator?.extractVisualSource()
        ?: componentType
}

private fun String.extractVisualSource(): String? {
    return extractLocatorDetail(VISUAL_SOURCE_LOCATOR_KEY)
        ?: extractLocatorDetail(IMAGE_VECTOR_LOCATOR_KEY)
        ?: extractLocatorDetail(PAINTER_LOCATOR_KEY)
}

private fun String.extractLocatorDetail(key: String): String? {
    val value = substringAfter("$key=", missingDelimiterValue = "")
    if (value.isBlank()) return null

    return value
        .substringBefore(", ")
        .removeSuffix("]")
        .takeIf { detail -> detail.isNotBlank() }
}

private fun revealFileInExplorer(path: String) {
    val file = File(path)
    if (!file.exists()) return
    runCatching {
        ProcessBuilder("explorer.exe", "/select,${file.absolutePath}").start()
    }
}

private fun activeFilterSummary(
    sourceFilter: IssueSourceFilter,
    severityFilter: Severity?,
    ruleFilter: String?
): String {
    return listOfNotNull(
        sourceFilter.takeIf { filter -> filter != IssueSourceFilter.ALL }?.label,
        severityFilter?.name,
        ruleFilter
    ).joinToString(" / ")
}

private enum class IssueSourceFilter(val label: String) {
    ALL(DesktopIssueReportText.ALL_SOURCES),
    COMPOSE(DesktopIssueReportText.COMPOSE),
    XML(DesktopIssueReportText.XML),
    RUNTIME(DesktopIssueReportText.RUNTIME),
    PROJECT(DesktopIssueReportText.PROJECT)
}

private const val COMPOSE_FUNCTION_LOCATOR_KEY = "composable"
private const val VISUAL_SOURCE_LOCATOR_KEY = "visualSource"
private const val IMAGE_VECTOR_LOCATOR_KEY = "imageVector"
private const val PAINTER_LOCATOR_KEY = "painter"
