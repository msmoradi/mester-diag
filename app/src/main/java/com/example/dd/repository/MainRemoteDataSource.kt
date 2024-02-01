package com.example.dd.repository

import com.example.dd.model.CreateRequestModel
import com.example.dd.model.CreateResponseModel
import com.example.dd.model.DeviceInfoModel
import com.example.dd.model.SubmitRequestModel
import com.example.dd.model.SubmitResponseModel
import com.example.dd.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class MainRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun sendData(data: DeviceInfoModel, ticket: String): CreateResponseModel =
        withContext(Dispatchers.IO) {
            val requestModel = CreateRequestModel(
                ticket = ticket,
                batteryHealth = data.batteryHealth,
                availableMemory = data.availableMemory,
                totalMemory = data.totalMemory,
                deviceModel = data.deviceModel,
                deviceBrand = data.deviceBrand,
                deviceBoard = data.deviceBoard,
                deviceManufacturer = data.deviceManufacturer,
                deviceProduct = data.deviceProduct,
                deviceHardware = data.deviceHardware,
                osVersion = data.osVersion
            )
            apiService.sendData(requestModel)
        }

    suspend fun finish(ticket: String): SubmitResponseModel = withContext(Dispatchers.IO) {
        val requestModel = SubmitRequestModel(ticket)
        apiService.finish(requestModel)
    }

    suspend fun uploadAudio(audioFilePath: MultipartBody.Part) = withContext(Dispatchers.IO) {
        apiService.uploadAudio(audioFilePath)
    }

}