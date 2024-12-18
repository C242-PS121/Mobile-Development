package com.dicoding.thriftify.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("message")
	val message: String,
)

data class Data(

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("access_token")
	val accessToken: String,

	@field:SerializedName("refresh_token")
	val refreshToken: String,
)
