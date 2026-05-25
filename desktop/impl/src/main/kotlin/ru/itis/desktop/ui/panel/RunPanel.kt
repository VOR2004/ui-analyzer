package ru.itis.desktop.ui.panel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.itis.desktop.text.DesktopPanelText
import ru.itis.desktop.ui.component.IconOnlyButton
import ru.itis.desktop.ui.component.Panel
import ru.itis.desktop.ui.component.RulesIcon

@Composable
fun RunPanel(
    status: String,
    isRunning: Boolean,
    rulesPanelVisible: Boolean,
    selectedRuleCount: Int,
    totalRuleCount: Int,
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
        }
    }
}
