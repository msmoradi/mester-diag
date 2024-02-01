package com.example.dd.model

import com.google.gson.annotations.SerializedName

data class CreateRequestModel(
    @SerializedName("ticket") val ticket: String,
    @SerializedName("battery_health") val batteryHealth: String,
    @SerializedName("available_memory") val availableMemory: String,
    @SerializedName("total_memory") val totalMemory: String,
    @SerializedName("device_model") val deviceModel: String,
    @SerializedName("device_brand") val deviceBrand: String,
    @SerializedName("device_board") val deviceBoard: String,
    @SerializedName("device_hardware") val deviceHardware: String,
    @SerializedName("device_manufacturer") val deviceManufacturer: String,
    @SerializedName("device_product") val deviceProduct: String,
    @SerializedName("os_version") val osVersion: String,
)
