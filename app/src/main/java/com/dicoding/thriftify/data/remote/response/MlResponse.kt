package com.dicoding.thriftify.data.remote.response

data class MlResponse(
    val message: String,
    val data: MlData
)

data class MlData(
    val usage: String,
    val type: String
)