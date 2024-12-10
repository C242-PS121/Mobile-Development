package com.dicoding.thriftify.data.remote.retrofit

import DetailProductResponse
import com.dicoding.thriftify.data.remote.request.LoginRequest
import com.dicoding.thriftify.data.remote.request.RegisterRequest
import com.dicoding.thriftify.data.remote.response.LoginResponse
import com.dicoding.thriftify.data.remote.response.LogoutResponse
import com.dicoding.thriftify.data.remote.response.ProductListResponse
import com.dicoding.thriftify.data.remote.response.RefreshTokenResponse
import com.dicoding.thriftify.data.remote.response.RegisterResponse
import com.dicoding.thriftify.data.remote.response.UploadProductResponse
import com.dicoding.thriftify.data.remote.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("users")
    suspend fun register(
        @Body requestBody: RegisterRequest
    ): RegisterResponse

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @PUT("auth/login")
    suspend fun refreshAccessToken(
        @Body requestBody: RequestBody
    ): RefreshTokenResponse

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") userId: String,
        @Header("Authorization") accessToken: String
    ): UserResponse

    @GET("products")
    suspend fun getAllProducts(
        @Header("Authorization") accessToken: String
    ): ProductListResponse

    @POST("v2/products")
    @Multipart
    suspend fun uploadProduct(
        @Part("owner_id") ownerId: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody?,
        @Part("description") description: RequestBody,
        @Part("clothing_type") clothingType: RequestBody,
        @Header("Authorization") accessToken: String
    ): UploadProductResponse

//    @GET("products/{id}")
//    suspend fun getProductDetails(
//        @Header("Authorization") accessToken: String,
//        @Path("id") productId: String
//    ): DetailProductResponse

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: String,
        @Header("Authorization") accessToken: String
    ): DetailProductResponse


    @POST("auth/logout")
    suspend fun logout(@Body requestBody: RequestBody): LogoutResponse

}