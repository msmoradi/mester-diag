package com.example.dd.repository

import com.example.dd.model.DeviceInfoModel
import com.example.dd.utils.DeviceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeviceLocalDataSource @Inject constructor(
    private val deviceUtils: DeviceUtils,
) {

    suspend fun collectDeviceInfo(): DeviceInfoModel {
        return withContext(Dispatchers.IO) {
            deviceUtils.collectDeviceInfo()
        }
    }

    suspend fun checkPort(): Boolean {
        return withContext(Dispatchers.IO) {
            deviceUtils.checkPort()
        }
    }
}