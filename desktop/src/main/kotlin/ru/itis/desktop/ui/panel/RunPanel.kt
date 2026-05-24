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
import androidx.compose.ui.unit.dp
import ru.itis.desktop.ui.component.Panel

@Composable
fun RunPanel(
    status: String,
    isRunning: Boolean,
    onRun: () -> Unit
) {
    Panel(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Status",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = status,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isRunning) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 14.dp))
            }
            Button(
                enabled = !isRunning,
                onClick = onRun
            ) {
                Text("Run analysis")
            }
        }
    }
}
