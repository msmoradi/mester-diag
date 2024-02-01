package com.example.dd.main

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dd.model.CreateResponseModel
import com.example.dd.model.DeviceInfoModel
import com.example.dd.repository.DeviceLocalDataSource
import com.example.dd.repository.MainRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val mainRemoteDataSource: MainRemoteDataSource,
    private val deviceLocalDataSource: DeviceLocalDataSource
) : ViewModel() {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioFile: File

    init {
        audioFile = File(context.filesDir, "audio_record.3gp")
    }

    private val _uiState = MutableStateFlow(
        UiState(ticket = savedStateHandle.get<String>("token").orEmpty())
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onStartClicked(ticket: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loading = true
                )
            }

            try {
                val deviceInfoModel: DeviceInfoModel =
                    deviceLocalDataSource.collectDeviceInfo().copy(
                        isPortHealthy = _uiState.value.portState,
                        micId = _uiState.value.audioId,
                        cameraId = _uiState.value.audioId,
                    )
                val result = mainRemoteDataSource.sendData(
                    deviceInfoModel,
                    ticket
                )
                _uiState.update {
                    it.copy(
                        data = result,
                        loading = false
                    )
                }
                _uiEvent.emit(UiEvent.Navigate("checkOut"))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage(e.message.orEmpty()))
                _uiState.update {
                    it.copy(
                        loading = false
                    )
                }
            }
        }
    }

    fun onFinishClicked(ticket: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loading = true
                )
            }

            try {
                val result = mainRemoteDataSource.submit(ticket)

                _uiState.update {
                    it.copy(
                        loading = false
                    )
                }

                _uiEvent.emit(UiEvent.OpenDeeplink(result.deepLink))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage(e.message.orEmpty()))

                _uiState.update {
                    it.copy(
                        loading = false
                    )
                }
            }
        }
    }

    fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile.absolutePath)
            try {
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    fun startPlaying() {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(audioFile.absolutePath)
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun stopPlaying() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun uploadAudio() {
        if (audioFile.exists()) {
            val requestFile = audioFile.asRequestBody("audio/3gpp".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("audio", audioFile.name, requestFile)

            viewModelScope.launch {
                try {
                    val response = mainRemoteDataSource.uploadAudio(body)
                    _uiState.update {
                        it.copy(
                            audioId = response.id
                        )
                    }
                    _uiEvent.emit(UiEvent.Navigate("port"))
                } catch (e: Exception) {
                    _uiEvent.emit(UiEvent.ShowMessage(e.message.orEmpty()))
                    e.printStackTrace()
                    // Handle exception
                }
            }
        }
    }

    fun onCheckPortClicked() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    portState = deviceLocalDataSource.checkPort()
                )
            }
        }
    }

    override fun onCleared() {
        stopPlaying()
        super.onCleared()
    }

}

sealed interface UiEvent {
    data class ShowMessage(val message: String) : UiEvent
    data class OpenDeeplink(val deepLink: String) : UiEvent
    data class Navigate(val destination: String) : UiEvent
}

data class UiState(
    val audioId: String = "",
    val portState: Boolean = false,
    val loading: Boolean = false,
    val data: CreateResponseModel? = null,
    val ticket: String = "",
)