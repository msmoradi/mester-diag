package com.example.dd.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun VoiceScreen(
    onNextClicked: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onStartPlaying: () -> Unit,
    onStopPlaying: () -> Unit,
) {

    val context = LocalContext.current

    var hasRecordPermission by remember { mutableStateOf(false) }
    var hasReadPermission by remember { mutableStateOf(false) }
    var hasWritePermission by remember { mutableStateOf(false) }

    // Check if the permissions are already granted
    LaunchedEffect(key1 = true) {
        hasRecordPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        hasReadPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        hasWritePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Prepare the permission launcher
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasRecordPermission =
                permissions[Manifest.permission.RECORD_AUDIO] ?: hasRecordPermission
            hasReadPermission =
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: hasReadPermission
            hasWritePermission =
                permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: hasWritePermission
        }
    )


    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    // UI
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "توضیحات دیباگ میکروفون")
            Button(
                onClick = {

                    when {
                        hasRecordPermission && hasReadPermission && hasWritePermission -> {

                            if (isRecording) {
                                onStopRecording()
                                isRecording = false
                            } else {
                                onStartRecording()
                                isRecording = true
                            }
                            // Permission is granted, you can start recording
                        }

                        else -> {
                            // Permission is not granted, request it
                            permissionsLauncher.launch(
                                arrayOf(
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            )
                        }
                    }
                }
            ) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }
            Button(
                onClick = {
                    if (isPlaying) {
                        onStopPlaying()
                        isPlaying = false
                    } else {
                        onStartPlaying()
                        isPlaying = true
                    }
                },
                enabled = !isRecording // Disable the button while recording
            ) {
                Text(if (isPlaying) "Stop Playing" else "Play Recording")
            }
            Button(onClick = {
                onNextClicked()
            }) {
                Text(text = "مرحله بعد")
            }
        }
    }
}
