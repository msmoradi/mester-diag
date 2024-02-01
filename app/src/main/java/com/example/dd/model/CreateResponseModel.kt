package com.example.dd.model

import com.google.gson.annotations.SerializedName

data class CreateResponseModel(
    @SerializedName("battery_health") val batteryHealth: String,
    @SerializedName("available_memory") val availableMemory: Long,
    @SerializedName("total_memory") val totalMemory: Long,
    @SerializedName("total_internal_memory") val totalInternalMemory: Long,
    @SerializedName("is_port_healthy") val isPortHealthy: Boolean,
    @SerializedName("device_model") val deviceModel: String,
    @SerializedName("device_brand") val deviceBrand: String,
    @SerializedName("device_board") val deviceBoard: String,
    @SerializedName("device_hardware") val deviceHardware: String,
    @SerializedName("device_manufacturer") val deviceManufacturer: String,
    @SerializedName("device_product") val deviceProduct: String,
    @SerializedName("os_version") val osVersion: String,
    @SerializedName("image") val imageUrl: String,
)
