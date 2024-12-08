package com.dicoding.thriftify.data.remote.response

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: RefreshTokenData
)

data class RefreshTokenData(
    @SerializedName("access_token")
    val accessToken: String
)
