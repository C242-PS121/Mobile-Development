package com.dicoding.thriftify.data.remote.retrofit

import com.dicoding.thriftify.data.remote.request.LoginRequest
import com.dicoding.thriftify.data.remote.request.RefreshTokenRequest
import com.dicoding.thriftify.data.remote.request.RegisterRequest
import com.dicoding.thriftify.data.remote.response.LoginResponse
import com.dicoding.thriftify.data.remote.response.LogoutResponse
import com.dicoding.thriftify.data.remote.response.RefreshTokenResponse
import com.dicoding.thriftify.data.remote.response.RegisterResponse
import com.dicoding.thriftify.data.remote.response.UserResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("users")
    suspend fun register(
        @Body requestBody: RegisterRequest
    ): RegisterResponse

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

//    @PUT("auth/login")
//    suspend fun refreshToken(@Body refreshToken: RefreshTokenRequest): RefreshTokenResponse

    @PUT("auth/login")
    suspend fun refreshAccessToken(
        @Body requestBody: RequestBody
    ): RefreshTokenResponse


    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") userId: String,
        @Header("Authorization") accessToken: String
    ): UserResponse

    @POST("auth/logout")
    suspend fun logout(@Body requestBody: RequestBody): LogoutResponse

}