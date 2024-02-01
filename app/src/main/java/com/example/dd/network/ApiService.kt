package com.example.dd.network

import com.example.dd.model.CreateRequestModel
import com.example.dd.model.CreateResponseModel
import com.example.dd.model.SubmitRequestModel
import com.example.dd.model.SubmitResponseModel
import com.example.dd.model.UploadAudioResponseModel
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @Headers("Content-Type: audio/3gpp")
    @POST("upload/")
    suspend fun uploadAudio(@Part file: MultipartBody.Part): UploadAudioResponseModel

    @POST("submit-report/")
    suspend fun finish(@Body requestModel: SubmitRequestModel): SubmitResponseModel

    @POST("create-report/")
    suspend fun sendData(@Body requestModel: CreateRequestModel): CreateResponseModel

}