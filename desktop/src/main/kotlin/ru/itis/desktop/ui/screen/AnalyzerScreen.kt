package ru.itis.desktop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import ru.itis.desktop.analysis.DesktopAnalysisRunner
import ru.itis.desktop.analysis.DesktopTheme
import ru.itis.desktop.analysis.RuleCatalog
import ru.itis.desktop.analysis.RuntimeSnapshotSource
import ru.itis.desktop.analysis.StaticSourceTarget
import ru.itis.desktop.dialog.FileKitDesktopFileDialog
import ru.itis.desktop.ui.panel.RuleSelectionPanel
import ru.itis.desktop.ui.panel.RunPanel
import ru.itis.desktop.ui.panel.SettingsPanel

@Composable
fun AnalyzerScreen(
    theme: DesktopTheme,
    onThemeChange: (DesktopTheme) -> Unit
) {
    val runner = remember { DesktopAnalysisRunner() }
    val fileDialog = remember { FileKitDesktopFileDialog() }
    val coroutineScope = rememberCoroutineScope()

    var projectPath by remember { mutableStateOf("") }
    var outputPath by remember { mutableStateOf("analysis-report.json") }
    var mode by remember { mutableStateOf(DesktopAnalysisMode.STATIC) }
    var staticTarget by remember { mutableStateOf(StaticSourceTarget.BOTH) }
    var runtimeSource by remember { mutableStateOf(RuntimeSnapshotSource.ADB) }
    var runtimeSnapshotPath by remember { mutableStateOf("") }
    var adbSerial by remember { mutableStateOf("") }
    var selectedRuleIds by remember { mutableStateOf(emptySet<String>()) }
    var status by remember { mutableStateOf("Ready for analysis.") }
    var isRunning by remember { mutableStateOf(false) }

    val rules = RuleCatalog.descriptors(mode, staticTarget)

    LaunchedEffect(mode, staticTarget) {
        selectedRuleIds = rules.map { rule -> rule.id }.toSet()
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
            theme = theme,
            onThemeChange = onThemeChange,
            onSelectProjectDirectory = {
                coroutineScope.launch {
                    fileDialog.selectProjectDirectory()?.let { value -> projectPath = value }
                }
            },
            onSelectReportOutputFile = {
                coroutineScope.launch {
                    fileDialog.selectReportOutputFile()?.let { value -> outputPath = value }
                }
            },
            onSelectRuntimeSnapshotFile = {
                coroutineScope.launch {
                    fileDialog.selectRuntimeSnapshotFile()?.let { value -> runtimeSnapshotPath = value }
                }
            },
            modifier = Modifier
                .width(390.dp)
                .fillMaxHeight()
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            RuleSelectionPanel(
                rules = rules,
                selectedRuleIds = selectedRuleIds,
                onSelectedRuleIdsChange = { value -> selectedRuleIds = value },
                modifier = Modifier.weight(1f)
            )
            RunPanel(
                status = status,
                isRunning = isRunning,
                onRun = {
                    isRunning = true
                    status = "Analysis started..."
                    val request = DesktopAnalysisRequest(
                        projectPath = projectPath,
                        outputPath = outputPath.ifBlank { "analysis-report.json" },
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
                            onSuccess = { value ->
                                "Done: components=${value.componentCount}, issues=${value.issueCount}, report=${value.outputPath}"
                            },
                            onFailure = { error -> "Error: ${error.message}" }
                        )
                        isRunning = false
                    }
                }
            )
        }
    }
}
