package com.example.dd.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dd.screen.CameraScreen
import com.example.dd.screen.CheckoutScreen
import com.example.dd.screen.HomeScreen
import com.example.dd.screen.InfoScreen
import com.example.dd.screen.PortScreen
import com.example.dd.screen.VoiceScreen
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
                            onNextClicked = mainViewModel::uploadAudio,
                            onStartRecording = mainViewModel::startRecording,
                            onStopRecording = mainViewModel::stopRecording,
                            onStartPlaying = mainViewModel::startPlaying,
                            onStopPlaying = mainViewModel::stopPlaying,
                        )
                    }
                    composable("port") {
                        PortScreen(
                            onCheckPortClicked = mainViewModel::onCheckPortClicked,
                            portState = if (uiState.portState) "وصله" else "وصل نیست",
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
