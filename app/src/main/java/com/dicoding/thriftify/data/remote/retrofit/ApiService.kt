package com.dicoding.thriftify.data.remote.retrofit

import com.dicoding.thriftify.data.remote.request.LoginRequest
import com.dicoding.thriftify.data.remote.request.LogoutRequest
import com.dicoding.thriftify.data.remote.request.RegisterRequest
import com.dicoding.thriftify.data.remote.response.LoginResponse
import com.dicoding.thriftify.data.remote.response.LogoutResponse
import com.dicoding.thriftify.data.remote.response.RegisterResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("users")
    suspend fun register(@Body requestBody: RegisterRequest
    ): RegisterResponse

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

//    @DELETE("auth/logout")
//    suspend fun logout(@Body logoutRequest: LogoutRequest): LogoutResponse

//    @DELETE("/auth/logout")
//    fun logout(@Query("refreshToken") refreshToken: String): Call<LogoutResponse>

//    @FormUrlEncoded
//    @DELETE("auth/logout")
//    suspend fun logout(@Field("refreshToken") refreshToken: String): LogoutResponse

//    @DELETE("auth/logout")
//    suspend fun logout(@Query("refreshToken") refreshToken: String): LogoutResponse

    @DELETE("auth/logout")
    suspend fun logout(@Body requestBody: RequestBody): LogoutResponse

}