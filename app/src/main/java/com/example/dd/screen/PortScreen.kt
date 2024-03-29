package com.example.dd.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PortScreen(
    portState: String,
    onCheckPortClicked: () -> Unit,
    onNextClicked: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "توضیحات دیباگ پورت شارژ")
            Text(text = portState)
            Button(onClick = onCheckPortClicked) {
                Text(text = "بررسی وضعیت")
            }
            Button(onClick = onNextClicked) {
                Text(text = "مرحله بعد")
            }
        }
    }
}