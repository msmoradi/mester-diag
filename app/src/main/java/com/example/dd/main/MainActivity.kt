package com.example.dd.main

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.dd.model.CreateResponseModel
import com.example.dd.ui.theme.DDTheme
import com.example.dd.utils.collectAsEvent
import dagger.hilt.android.AndroidEntryPoint

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
            val navController = rememberNavController()

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

                    is UiEvent.Navigate -> {
                        navController.navigate(uiEvent.destination)
                    }
                }
            }


            DDTheme {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onNextClicked = {
                                navController.navigate("voice")
                            }
                        )
                    }
                    composable("voice") {
                        VoiceScreen(
                            onNextClicked = {
                                navController.navigate("port")
                            }
                        )
                    }
                    composable("port") {
                        PortScreen(
                            onNextClicked = {
                                navController.navigate("camera")
                            }
                        )
                    }
                    composable("camera") {
                        CameraScreen(
                            onNextClicked = {
                                navController.navigate("info")
                            }
                        )
                    }
                    composable("info") {
                        InfoScreen(
                            loading = uiState.loading,
                            onNextClicked = {
                                mainViewModel.onStartClicked(ticket)
                            }
                        )
                    }
                    composable("checkOut") {
                        CheckoutScreen(
                            dataMap = uiState.data!!,
                            onNextClicked = {
                                mainViewModel.onFinishClicked(ticket)
                            }
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun HomeScreen(
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
            Text(text = "توضیحات شروع برنامه")
            Button(onClick = onNextClicked) {
                Text(text = "شروع")
            }
        }
    }
}


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


@Composable
fun PortScreen(
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
            Text(text = "وضعیت اتصال")
            Button(onClick = onNextClicked) {
                Text(text = "مرحله بعد")
            }
        }
    }
}


@Composable
fun CameraScreen(
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
            Text(text = "توضیحات دیباگ دوربین")

            Button(onClick = onNextClicked) {
                Text(text = "مرحله بعد")
            }
        }
    }
}

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

@Composable
fun CheckoutScreen(
    onNextClicked: () -> Unit,
    dataMap: CreateResponseModel,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "توضیحات نهایی")
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
            Button(onClick = onNextClicked) {
                Text(text = "ارسال و بازگشت به دیوار")
            }
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
