package com.example.dd.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dd.model.CreateResponseModel
import com.example.dd.model.DeviceInfoModel
import com.example.dd.repository.DeviceLocalDataSource
import com.example.dd.repository.MainRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mainRemoteDataSource: MainRemoteDataSource,
    private val deviceLocalDataSource: DeviceLocalDataSource
) : ViewModel() {

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
                val deviceInfoModel: DeviceInfoModel = deviceLocalDataSource.collectDeviceInfo()
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
                val result = mainRemoteDataSource.finish(ticket)

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


}

sealed interface UiEvent {
    data class ShowMessage(val message: String) : UiEvent
    data class OpenDeeplink(val deepLink: String) : UiEvent
}

data class UiState(
    val loading: Boolean = false,
    val data: CreateResponseModel? = null,
    val ticket: String = "",
)