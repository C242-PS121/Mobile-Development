package com.dicoding.thriftify.data.remote.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullname: String,
    val phone: String
)