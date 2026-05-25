package ru.itis.desktop.ui.panel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.itis.desktop.analysis.DesktopAnalysisMode
import ru.itis.desktop.analysis.RuntimeSnapshotSource
import ru.itis.desktop.analysis.StaticSourceTarget
import ru.itis.desktop.ui.component.Panel
import ru.itis.desktop.ui.component.PathField
import ru.itis.desktop.ui.component.SectionTitle
import ru.itis.desktop.ui.component.SelectableOptionCard

@Composable
fun SettingsPanel(
    projectPath: String,
    onProjectPathChange: (String) -> Unit,
    outputPath: String,
    onOutputPathChange: (String) -> Unit,
    mode: DesktopAnalysisMode,
    onModeChange: (DesktopAnalysisMode) -> Unit,
    staticTarget: StaticSourceTarget,
    onStaticTargetChange: (StaticSourceTarget) -> Unit,
    runtimeSource: RuntimeSnapshotSource,
    onRuntimeSourceChange: (RuntimeSnapshotSource) -> Unit,
    runtimeSnapshotPath: String,
    onRuntimeSnapshotPathChange: (String) -> Unit,
    adbSerial: String,
    onAdbSerialChange: (String) -> Unit,
    onSelectProjectDirectory: () -> Unit,
    onSelectReportOutputFile: () -> Unit,
    onSelectRuntimeSnapshotFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Panel(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "UI Analyzer",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Desktop control panel",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            PathField(
                label = "Android project",
                value = projectPath,
                onValueChange = onProjectPathChange,
                onBrowse = onSelectProjectDirectory
            )
            PathField(
                label = "Report output",
                value = outputPath,
                onValueChange = onOutputPathChange,
                onBrowse = onSelectReportOutputFile
            )

            SectionTitle("Mode")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SelectableOptionCard(
                    title = "Static",
                    description = "Analyze XML and Compose sources.",
                    selected = mode == DesktopAnalysisMode.STATIC,
                    onClick = { onModeChange(DesktopAnalysisMode.STATIC) },
                    iconText = "ST",
                    modifier = Modifier.weight(1f),
                    minHeight = 132.dp
                )
                SelectableOptionCard(
                    title = "Runtime",
                    description = "Capture actual UI bounds from device.",
                    selected = mode == DesktopAnalysisMode.RUNTIME,
                    onClick = { onModeChange(DesktopAnalysisMode.RUNTIME) },
                    iconText = "RT",
                    modifier = Modifier.weight(1f),
                    minHeight = 132.dp
                )
            }

            if (mode == DesktopAnalysisMode.STATIC) {
                StaticTargetOptions(
                    staticTarget = staticTarget,
                    onStaticTargetChange = onStaticTargetChange
                )
            } else {
                RuntimeSourceOptions(
                    runtimeSource = runtimeSource,
                    onRuntimeSourceChange = onRuntimeSourceChange,
                    runtimeSnapshotPath = runtimeSnapshotPath,
                    onRuntimeSnapshotPathChange = onRuntimeSnapshotPathChange,
                    adbSerial = adbSerial,
                    onAdbSerialChange = onAdbSerialChange,
                    onSelectRuntimeSnapshotFile = onSelectRuntimeSnapshotFile
                )
            }
        }
    }
}

@Composable
private fun StaticTargetOptions(
    staticTarget: StaticSourceTarget,
    onStaticTargetChange: (StaticSourceTarget) -> Unit
) {
    SectionTitle("Source")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SelectableOptionCard(
            title = "XML + Compose",
            description = "Run both static rule sets.",
            selected = staticTarget == StaticSourceTarget.BOTH,
            onClick = { onStaticTargetChange(StaticSourceTarget.BOTH) },
            iconText = "A"
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SelectableOptionCard(
                title = "XML",
                description = "Layout resources only.",
                selected = staticTarget == StaticSourceTarget.XML,
                onClick = { onStaticTargetChange(StaticSourceTarget.XML) },
                iconText = "X",
                modifier = Modifier.weight(1f)
            )
            SelectableOptionCard(
                title = "Compose",
                description = "Kotlin UI sources only.",
                selected = staticTarget == StaticSourceTarget.COMPOSE,
                onClick = { onStaticTargetChange(StaticSourceTarget.COMPOSE) },
                iconText = "C",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RuntimeSourceOptions(
    runtimeSource: RuntimeSnapshotSource,
    onRuntimeSourceChange: (RuntimeSnapshotSource) -> Unit,
    runtimeSnapshotPath: String,
    onRuntimeSnapshotPathChange: (String) -> Unit,
    adbSerial: String,
    onAdbSerialChange: (String) -> Unit,
    onSelectRuntimeSnapshotFile: () -> Unit
) {
    SectionTitle("Runtime snapshot")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SelectableOptionCard(
            title = "ADB",
            description = "Capture from emulator or device.",
            selected = runtimeSource == RuntimeSnapshotSource.ADB,
            onClick = { onRuntimeSourceChange(RuntimeSnapshotSource.ADB) },
            iconText = "ADB",
            modifier = Modifier.weight(1f),
            minHeight = 120.dp
        )
        SelectableOptionCard(
            title = "File",
            description = "Use saved runtime snapshot.",
            selected = runtimeSource == RuntimeSnapshotSource.SNAPSHOT_FILE,
            onClick = { onRuntimeSourceChange(RuntimeSnapshotSource.SNAPSHOT_FILE) },
            iconText = "JS",
            modifier = Modifier.weight(1f),
            minHeight = 120.dp
        )
    }
    if (runtimeSource == RuntimeSnapshotSource.ADB) {
        OutlinedTextField(
            value = adbSerial,
            onValueChange = onAdbSerialChange,
            label = { Text("ADB serial, optional") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    } else {
        PathField(
            label = "Runtime snapshot file",
            value = runtimeSnapshotPath,
            onValueChange = onRuntimeSnapshotPathChange,
            onBrowse = onSelectRuntimeSnapshotFile
        )
    }
}
