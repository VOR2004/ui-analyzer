package ru.itis.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComposeDemoScreen() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text(
            text = "Compose title",
            fontSize = 24.sp,
            color = Color.Black
        )

        Row(modifier = Modifier.padding(8.dp)) {
            Button(
                modifier = Modifier.size(48.dp),
                onClick = {}
            ) {
                Text("Open")
            }

            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = "Demo image"
            )

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null
            )

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
            }
        }
    }
}
