package ru.itis.desktop.ui.panel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.itis.desktop.analysis.DesktopAnalysisMode
import ru.itis.desktop.analysis.DesktopTheme
import ru.itis.desktop.analysis.RuntimeSnapshotSource
import ru.itis.desktop.analysis.StaticSourceTarget
import ru.itis.desktop.ui.component.Panel
import ru.itis.desktop.ui.component.PathField
import ru.itis.desktop.ui.component.RadioOption
import ru.itis.desktop.ui.component.SectionTitle

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
    theme: DesktopTheme,
    onThemeChange: (DesktopTheme) -> Unit,
    onSelectProjectDirectory: () -> Unit,
    onSelectReportOutputFile: () -> Unit,
    onSelectRuntimeSnapshotFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Panel(modifier = modifier) {
        Text(
            text = "UI Analyzer",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Desktop control panel",
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        RadioOption(
            selected = mode == DesktopAnalysisMode.STATIC,
            text = "Static analysis",
            onClick = { onModeChange(DesktopAnalysisMode.STATIC) }
        )
        RadioOption(
            selected = mode == DesktopAnalysisMode.RUNTIME,
            text = "Runtime analysis",
            onClick = { onModeChange(DesktopAnalysisMode.RUNTIME) }
        )

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

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dark theme")
            Switch(
                checked = theme == DesktopTheme.DARK,
                onCheckedChange = { checked ->
                    onThemeChange(if (checked) DesktopTheme.DARK else DesktopTheme.LIGHT)
                }
            )
        }
    }
}

@Composable
private fun StaticTargetOptions(
    staticTarget: StaticSourceTarget,
    onStaticTargetChange: (StaticSourceTarget) -> Unit
) {
    SectionTitle("Source")
    RadioOption(
        selected = staticTarget == StaticSourceTarget.BOTH,
        text = "XML + Compose",
        onClick = { onStaticTargetChange(StaticSourceTarget.BOTH) }
    )
    RadioOption(
        selected = staticTarget == StaticSourceTarget.XML,
        text = "Only XML",
        onClick = { onStaticTargetChange(StaticSourceTarget.XML) }
    )
    RadioOption(
        selected = staticTarget == StaticSourceTarget.COMPOSE,
        text = "Only Compose",
        onClick = { onStaticTargetChange(StaticSourceTarget.COMPOSE) }
    )
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
    RadioOption(
        selected = runtimeSource == RuntimeSnapshotSource.ADB,
        text = "Capture from emulator/device via ADB",
        onClick = { onRuntimeSourceChange(RuntimeSnapshotSource.ADB) }
    )
    RadioOption(
        selected = runtimeSource == RuntimeSnapshotSource.SNAPSHOT_FILE,
        text = "Use snapshot file",
        onClick = { onRuntimeSourceChange(RuntimeSnapshotSource.SNAPSHOT_FILE) }
    )
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
