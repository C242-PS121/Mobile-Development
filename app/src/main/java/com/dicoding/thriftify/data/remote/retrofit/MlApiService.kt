package com.dicoding.thriftify.data.remote.retrofit

import com.dicoding.thriftify.data.remote.response.MlResponse
import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MlApiService {
    @POST("classify")
    @Multipart
    suspend fun classifyImage(
        @Header("Authorization") accessToken: String,
        @Part image: MultipartBody.Part
    ): MlResponse
}