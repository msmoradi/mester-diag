package com.example.dd.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun VoiceScreen(
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
            Text(text = "توضیحات دیباگ میکروفون")
            Button(onClick = { /*TODO*/ }) {
                Text(text = "شروع ضبط صدا")
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text = "پخش صدای ضبط شده")
            }
            Button(onClick = onNextClicked) {
                Text(text = "مرحله بعد")
            }
        }
    }
}
