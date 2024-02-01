package com.example.dd.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.example.dd.model.CreateResponseModel
import com.example.dd.ui.theme.DDTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appLinkIntent: Intent = intent
        val appLinkData: Uri? = appLinkIntent.data
        val ticket = appLinkData?.getQueryParameter("ticket").orEmpty()

        setContent {
            val mainViewModel: MainViewModel by viewModels()
            val context = LocalContext.current

            val uiState by mainViewModel.uiState.collectAsState()
            mainViewModel.uiEvent.collectAsEvent { uiEvent ->
                when (uiEvent) {
                    is UiEvent.OpenDeeplink -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiEvent.deepLink))

                        // Check if there is an app that can handle this intent
                        val activities = context.packageManager.queryIntentActivities(intent, 0)
                        val isIntentSafe: Boolean = activities.isNotEmpty()

                        if (isIntentSafe) {
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(
                                context,
                                "No application available to open this link",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, uiEvent.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            DDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(
                        deviceInfoMap = uiState.data,
                        loading = uiState.loading,
                        onStartClicked = {
                            mainViewModel.onStartClicked(ticket)
                        },
                        onFinishClicked = {
                            mainViewModel.onFinishClicked(ticket)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    deviceInfoMap: CreateResponseModel?,
    onStartClicked: () -> Unit,
    onFinishClicked: () -> Unit,
    loading: Boolean,
) {

    val context = LocalContext.current

    val permissionState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // Handle the result, isGranted is true if the permission was granted
            if (isGranted) {
                onStartClicked()
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


    if (loading) {
        Box(
            modifier = modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (deviceInfoMap != null) {
        InfoListScreen(
            modifier = modifier,
            dataMap = deviceInfoMap,
            onFinishClicked = onFinishClicked
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    if (isPermissionGranted) {
                        onStartClicked()
                        // Permission is already granted; proceed with the action that requires the permission
                    } else {
                        // If not, request the permission
                        permissionState.launch(Manifest.permission.READ_PHONE_STATE)
                    }
                }) {
                Text(if (isPermissionGranted) "Permission Granted" else "Request Permission")
            }
        }

    }
}

@Composable
fun InfoListScreen(
    modifier: Modifier = Modifier,
    dataMap: CreateResponseModel,
    onFinishClicked: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    PaddingValues(
                        horizontal = 16.dp, vertical = 16.dp
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                modifier = Modifier.size(200.dp),
                model = dataMap.imageUrl,
                contentDescription = "Translated description of what the image contains"
            )
            ListItemView("batteryHealth", dataMap.batteryHealth)
            ListItemView("availableMemory", dataMap.availableMemory)
            ListItemView("totalMemory", dataMap.totalMemory)
            ListItemView("deviceModel", dataMap.deviceModel)
            ListItemView("deviceBrand", dataMap.deviceBrand)
            ListItemView("deviceBoard", dataMap.deviceBoard)
            ListItemView("deviceHardware", dataMap.deviceHardware)
            ListItemView("deviceManufacturer", dataMap.deviceManufacturer)
            ListItemView("deviceProduct", dataMap.deviceProduct)
            ListItemView("osVersion", dataMap.osVersion)
        }
        Button(onClick = onFinishClicked) {
            Text(text = "Finish")
        }
    }
}

@Composable
fun ListItemView(key: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = key, fontSize = 14.sp)
        Text(text = value, fontSize = 14.sp)
    }
}

@Composable
fun <T> Flow<T>.collectAsEvent(
    key: Any = Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend (T) -> Unit,
) {
    LaunchedEffect(key) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = minActiveState) {
            if (context == EmptyCoroutineContext) {
                this@collectAsEvent.collectLatest {
                    block.invoke(it)
                }
            } else {
                withContext(context) {
                    this@collectAsEvent.collectLatest {
                        block.invoke(it)
                    }
                }
            }
        }
    }
}
