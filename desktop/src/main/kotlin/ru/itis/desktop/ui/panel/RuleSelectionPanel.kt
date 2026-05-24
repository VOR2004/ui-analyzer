package ru.itis.desktop.ui.panel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.itis.desktop.analysis.RuleDescriptor
import ru.itis.desktop.ui.component.Panel
import ru.itis.desktop.ui.component.RuleRow

@Composable
fun RuleSelectionPanel(
    rules: List<RuleDescriptor>,
    selectedRuleIds: Set<String>,
    onSelectedRuleIdsChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Panel(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Rules",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Selected ${selectedRuleIds.size} of ${rules.size}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    onSelectedRuleIdsChange(rules.map { rule -> rule.id }.toSet())
                }) {
                    Text("Select all")
                }
                OutlinedButton(onClick = { onSelectedRuleIdsChange(emptySet()) }) {
                    Text("Clear")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rules.forEach { rule ->
                RuleRow(
                    rule = rule,
                    checked = rule.id in selectedRuleIds,
                    onCheckedChange = { checked ->
                        onSelectedRuleIdsChange(
                            if (checked) {
                                selectedRuleIds + rule.id
                            } else {
                                selectedRuleIds - rule.id
                            }
                        )
                    }
                )
            }
        }
    }
}
