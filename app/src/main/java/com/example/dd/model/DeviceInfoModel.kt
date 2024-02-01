package com.example.dd.model

data class DeviceInfoModel(
    val batteryHealth: String,
    val availableMemory: String,
    val totalMemory: String,
    val deviceModel: String,
    val deviceBrand: String,
    val deviceBoard: String,
    val deviceManufacturer: String,
    val deviceProduct: String,
    val deviceHardware: String,
    val osVersion: String,
)
