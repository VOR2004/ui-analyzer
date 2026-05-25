package ru.itis.desktop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.itis.desktop.analysis.DesktopAnalysisMode
import ru.itis.desktop.analysis.DesktopAnalysisRequest
import ru.itis.desktop.analysis.DefaultDesktopAnalysisRunner
import ru.itis.desktop.analysis.DefaultDesktopRuleRegistry
import ru.itis.desktop.analysis.RuntimeSnapshotSource
import ru.itis.desktop.analysis.StaticSourceTarget
import ru.itis.desktop.dialog.FileKitDesktopFileDialog
import ru.itis.desktop.text.DesktopAnalysisText
import ru.itis.desktop.text.DesktopAppText
import ru.itis.desktop.ui.app.DesktopTitleBarActions
import ru.itis.desktop.ui.component.ResizeHandle
import ru.itis.desktop.ui.panel.RuleSelectionPanel
import ru.itis.desktop.ui.panel.RunPanel
import ru.itis.desktop.ui.panel.SettingsPanel

@Composable
fun AnalyzerScreen(
    onTitleBarActionsChange: (DesktopTitleBarActions) -> Unit
) {
    val ruleRegistry = remember { DefaultDesktopRuleRegistry() }
    val runner = remember { DefaultDesktopAnalysisRunner(ruleRegistry = ruleRegistry) }
    val fileDialog = remember { FileKitDesktopFileDialog() }
    val coroutineScope = rememberCoroutineScope()

    var projectPath by remember { mutableStateOf(DesktopAppText.EMPTY_VALUE) }
    var outputPath by remember { mutableStateOf(DesktopAppText.DEFAULT_REPORT_FILE) }
    var mode by remember { mutableStateOf(DesktopAnalysisMode.STATIC) }
    var staticTarget by remember { mutableStateOf(StaticSourceTarget.BOTH) }
    var runtimeSource by remember { mutableStateOf(RuntimeSnapshotSource.ADB) }
    var runtimeSnapshotPath by remember { mutableStateOf(DesktopAppText.EMPTY_VALUE) }
    var adbSerial by remember { mutableStateOf(DesktopAppText.EMPTY_VALUE) }
    var selectedRuleIds by remember { mutableStateOf(emptySet<String>()) }
    var status by remember { mutableStateOf(DesktopAnalysisText.READY_STATUS) }
    var isRunning by remember { mutableStateOf(false) }
    var rulesPanelVisible by remember { mutableStateOf(true) }
    var rulesPanelWidth by remember { mutableStateOf(460.dp) }

    val rules = ruleRegistry.descriptors(mode, staticTarget)

    LaunchedEffect(mode, staticTarget) {
        selectedRuleIds = rules.map { rule -> rule.id }.toSet()
    }

    fun selectProjectDirectory() {
        coroutineScope.launch {
            fileDialog.selectProjectDirectory()?.let { value -> projectPath = value }
        }
    }

    fun selectReportOutputFile() {
        coroutineScope.launch {
            fileDialog.selectReportOutputFile()?.let { value -> outputPath = value }
        }
    }

    fun selectRuntimeSnapshotFile() {
        coroutineScope.launch {
            fileDialog.selectRuntimeSnapshotFile()?.let { value -> runtimeSnapshotPath = value }
        }
    }

    fun runAnalysis() {
        if (isRunning) return

        isRunning = true
        status = DesktopAnalysisText.STARTED_STATUS
        val request = DesktopAnalysisRequest(
            projectPath = projectPath,
            outputPath = outputPath.ifBlank { DesktopAppText.DEFAULT_REPORT_FILE },
            mode = mode,
            staticTarget = staticTarget,
            runtimeSource = runtimeSource,
            runtimeSnapshotPath = runtimeSnapshotPath,
            adbSerial = adbSerial.takeIf { value -> value.isNotBlank() },
            selectedRuleIds = selectedRuleIds
        )
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { runner.run(request) }
            }
            status = result.fold(
                onSuccess = { value -> DesktopAnalysisText.success(value) },
                onFailure = { error -> DesktopAnalysisText.failure(error) }
            )
            isRunning = false
        }
    }

    LaunchedEffect(isRunning, rulesPanelVisible, projectPath, outputPath, mode, staticTarget, runtimeSource, runtimeSnapshotPath, adbSerial, selectedRuleIds) {
        onTitleBarActionsChange(
            DesktopTitleBarActions(
                onOpenProject = ::selectProjectDirectory,
                onSelectReportOutput = ::selectReportOutputFile,
                onToggleRules = { rulesPanelVisible = !rulesPanelVisible },
                onRunAnalysis = ::runAnalysis,
                isRunning = isRunning,
                rulesPanelVisible = rulesPanelVisible
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(18.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        SettingsPanel(
            projectPath = projectPath,
            onProjectPathChange = { value -> projectPath = value },
            outputPath = outputPath,
            onOutputPathChange = { value -> outputPath = value },
            mode = mode,
            onModeChange = { value -> mode = value },
            staticTarget = staticTarget,
            onStaticTargetChange = { value -> staticTarget = value },
            runtimeSource = runtimeSource,
            onRuntimeSourceChange = { value -> runtimeSource = value },
            runtimeSnapshotPath = runtimeSnapshotPath,
            onRuntimeSnapshotPathChange = { value -> runtimeSnapshotPath = value },
            adbSerial = adbSerial,
            onAdbSerialChange = { value -> adbSerial = value },
            onSelectProjectDirectory = ::selectProjectDirectory,
            onSelectReportOutputFile = ::selectReportOutputFile,
            onSelectRuntimeSnapshotFile = ::selectRuntimeSnapshotFile,
            modifier = Modifier
                .widthIn(min = 360.dp, max = 420.dp)
                .width(390.dp)
                .fillMaxHeight()
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .widthIn(min = 360.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            RunPanel(
                status = status,
                isRunning = isRunning,
                rulesPanelVisible = rulesPanelVisible,
                selectedRuleCount = selectedRuleIds.size,
                totalRuleCount = rules.size,
                onToggleRulesPanel = { rulesPanelVisible = !rulesPanelVisible },
                onRun = ::runAnalysis
            )
        }

        if (rulesPanelVisible) {
            ResizeHandle(
                onDrag = { amount ->
                    rulesPanelWidth = (rulesPanelWidth - amount.dp).coerceIn(380.dp, 680.dp)
                }
            )
            RuleSelectionPanel(
                rules = rules,
                selectedRuleIds = selectedRuleIds,
                onSelectedRuleIdsChange = { value -> selectedRuleIds = value },
                onHide = { rulesPanelVisible = false },
                modifier = Modifier
                    .widthIn(min = 380.dp)
                    .width(rulesPanelWidth)
                    .fillMaxHeight()
            )
        }
    }
}
