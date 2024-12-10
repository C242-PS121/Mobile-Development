package com.dicoding.thriftify.data.remote.response

data class DetailProductResponse(
    val id: String,
    val ownerId: String,
    val mainImgUrl: String,
    val imageUrls: List<String>,
    val name: String,
    val price: Int,
    val description: String,
    val clothingType: String
)
