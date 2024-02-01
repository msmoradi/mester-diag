package com.example.dd.network

import com.example.dd.model.SubmitRequestModel
import com.example.dd.model.SubmitResponseModel
import com.example.dd.model.CreateRequestModel
import com.example.dd.model.CreateResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("submit-report/")
    suspend fun finish(@Body requestModel: SubmitRequestModel): SubmitResponseModel

    @POST("create-report/")
    suspend fun sendData(@Body requestModel: CreateRequestModel): CreateResponseModel

}