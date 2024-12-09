package com.dicoding.thriftify.data.remote.response

data class UploadProductResponse(
    val message: String,
    val data: ProductData
)

data class ProductData(
    val id: String
)
