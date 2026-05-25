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
import ru.itis.desktop.text.DesktopSettingsText
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
                text = DesktopSettingsText.TITLE,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = DesktopSettingsText.SUBTITLE,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            PathField(
                label = DesktopSettingsText.ANDROID_PROJECT,
                value = projectPath,
                onValueChange = onProjectPathChange,
                onBrowse = onSelectProjectDirectory
            )
            PathField(
                label = DesktopSettingsText.REPORT_OUTPUT,
                value = outputPath,
                onValueChange = onOutputPathChange,
                onBrowse = onSelectReportOutputFile
            )

            SectionTitle(DesktopSettingsText.MODE)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SelectableOptionCard(
                    title = DesktopSettingsText.STATIC_TITLE,
                    description = DesktopSettingsText.STATIC_DESCRIPTION,
                    selected = mode == DesktopAnalysisMode.STATIC,
                    onClick = { onModeChange(DesktopAnalysisMode.STATIC) },
                    iconText = DesktopSettingsText.STATIC_ICON,
                    modifier = Modifier.weight(1f),
                    minHeight = 132.dp
                )
                SelectableOptionCard(
                    title = DesktopSettingsText.RUNTIME_TITLE,
                    description = DesktopSettingsText.RUNTIME_DESCRIPTION,
                    selected = mode == DesktopAnalysisMode.RUNTIME,
                    onClick = { onModeChange(DesktopAnalysisMode.RUNTIME) },
                    iconText = DesktopSettingsText.RUNTIME_ICON,
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
    SectionTitle(DesktopSettingsText.SOURCE)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SelectableOptionCard(
            title = DesktopSettingsText.XML_COMPOSE_TITLE,
            description = DesktopSettingsText.XML_COMPOSE_DESCRIPTION,
            selected = staticTarget == StaticSourceTarget.BOTH,
            onClick = { onStaticTargetChange(StaticSourceTarget.BOTH) },
            iconText = DesktopSettingsText.XML_COMPOSE_ICON
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SelectableOptionCard(
                title = DesktopSettingsText.XML_TITLE,
                description = DesktopSettingsText.XML_DESCRIPTION,
                selected = staticTarget == StaticSourceTarget.XML,
                onClick = { onStaticTargetChange(StaticSourceTarget.XML) },
                iconText = DesktopSettingsText.XML_ICON,
                modifier = Modifier.weight(1f)
            )
            SelectableOptionCard(
                title = DesktopSettingsText.COMPOSE_TITLE,
                description = DesktopSettingsText.COMPOSE_DESCRIPTION,
                selected = staticTarget == StaticSourceTarget.COMPOSE,
                onClick = { onStaticTargetChange(StaticSourceTarget.COMPOSE) },
                iconText = DesktopSettingsText.COMPOSE_ICON,
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
    SectionTitle(DesktopSettingsText.RUNTIME_SNAPSHOT)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SelectableOptionCard(
            title = DesktopSettingsText.ADB_TITLE,
            description = DesktopSettingsText.ADB_DESCRIPTION,
            selected = runtimeSource == RuntimeSnapshotSource.ADB,
            onClick = { onRuntimeSourceChange(RuntimeSnapshotSource.ADB) },
            iconText = DesktopSettingsText.ADB_ICON,
            modifier = Modifier.weight(1f),
            minHeight = 120.dp
        )
        SelectableOptionCard(
            title = DesktopSettingsText.FILE_TITLE,
            description = DesktopSettingsText.FILE_DESCRIPTION,
            selected = runtimeSource == RuntimeSnapshotSource.SNAPSHOT_FILE,
            onClick = { onRuntimeSourceChange(RuntimeSnapshotSource.SNAPSHOT_FILE) },
            iconText = DesktopSettingsText.FILE_ICON,
            modifier = Modifier.weight(1f),
            minHeight = 120.dp
        )
    }
    if (runtimeSource == RuntimeSnapshotSource.ADB) {
        OutlinedTextField(
            value = adbSerial,
            onValueChange = onAdbSerialChange,
            label = { Text(DesktopSettingsText.ADB_SERIAL) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    } else {
        PathField(
            label = DesktopSettingsText.RUNTIME_SNAPSHOT_FILE,
            value = runtimeSnapshotPath,
            onValueChange = onRuntimeSnapshotPathChange,
            onBrowse = onSelectRuntimeSnapshotFile
        )
    }
}
