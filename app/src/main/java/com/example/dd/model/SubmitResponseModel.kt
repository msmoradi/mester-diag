package com.example.dd.model

import com.google.gson.annotations.SerializedName

data class SubmitResponseModel(
    @SerializedName("deep_link") val deepLink: String,
)
