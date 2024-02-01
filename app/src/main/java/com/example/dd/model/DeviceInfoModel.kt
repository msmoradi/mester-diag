package com.example.dd.model

import com.google.gson.annotations.SerializedName

data class DeviceInfoModel(
    val batteryHealth: String,
    val availableMemory: Long,
    val totalMemory: Long,
    val totalInternalMemory: Long = 0,
    val isPortHealthy: Boolean = false,
    val micId: String = "",
    val cameraId: String = "",
    val deviceModel: String,
    val deviceBrand: String,
    val deviceBoard: String,
    val deviceManufacturer: String,
    val deviceProduct: String,
    val deviceHardware: String,
    val osVersion: String,
)
