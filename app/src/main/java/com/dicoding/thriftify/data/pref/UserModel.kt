package com.dicoding.thriftify.data.pref

data class UserModel(
    val email: String,
    val userId: String,
    val accessToken: String,
    val refreshToken: String,
    val isLogin: Boolean = false
)