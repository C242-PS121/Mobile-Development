package com.dicoding.thriftify.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @field:SerializedName("data")
    val data: UserData
)

data class UserData(
    @field:SerializedName("email")
    val email: String? = null,
    @field:SerializedName("phone")
    val phone: String? = null,
    @field:SerializedName("fullname")
    val fullname: String? = null
)