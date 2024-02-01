package com.example.dd.screen

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    onNextClicked: () -> Unit,
    loading: Boolean,
) {

    val context = LocalContext.current

    val permissionState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // Handle the result, isGranted is true if the permission was granted
            if (isGranted) {
                onNextClicked()
                // Permission Granted
            } else {
                // Permission Denied
            }
        }
    )

    val isPermissionGranted = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.READ_PHONE_STATE
    ) == PackageManager.PERMISSION_GRANTED

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (loading) {
            Box(
                modifier = modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        if (isPermissionGranted) {
                            onNextClicked()
                            // Permission is already granted; proceed with the action that requires the permission
                        } else {
                            // If not, request the permission
                            permissionState.launch(android.Manifest.permission.READ_PHONE_STATE)
                        }
                    }) {
                    Text("بررسی و دریافت اطلاعات")
                }
            }

        }
    }
}