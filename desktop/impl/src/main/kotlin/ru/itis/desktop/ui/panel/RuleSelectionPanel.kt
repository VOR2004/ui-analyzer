package ru.itis.desktop.ui.panel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.itis.desktop.analysis.RuleDescriptor
import ru.itis.desktop.ui.component.ClearSelectionIcon
import ru.itis.desktop.ui.component.CollapseIcon
import ru.itis.desktop.ui.component.IconOnlyButton
import ru.itis.desktop.ui.component.Panel
import ru.itis.desktop.ui.component.RuleRow
import ru.itis.desktop.ui.component.SelectAllIcon

@Composable
fun RuleSelectionPanel(
    rules: List<RuleDescriptor>,
    selectedRuleIds: Set<String>,
    onSelectedRuleIdsChange: (Set<String>) -> Unit,
    onHide: () -> Unit,
    modifier: Modifier = Modifier
) {
    Panel(modifier = modifier.widthIn(min = 360.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Rule drawer",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Selected ${selectedRuleIds.size} of ${rules.size}.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier.width(118.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconOnlyButton(onClick = {
                    onSelectedRuleIdsChange(rules.map { rule -> rule.id }.toSet())
                }) { color -> SelectAllIcon(color = color) }
                IconOnlyButton(
                    onClick = { onSelectedRuleIdsChange(emptySet()) }
                ) { color -> ClearSelectionIcon(color = color) }
                IconOnlyButton(onClick = onHide) { color -> CollapseIcon(color = color) }
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
