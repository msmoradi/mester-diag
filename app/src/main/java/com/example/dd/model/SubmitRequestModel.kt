package com.example.dd.model

import com.google.gson.annotations.SerializedName

data class SubmitRequestModel(
    @SerializedName("ticket") val ticket: String,
)
