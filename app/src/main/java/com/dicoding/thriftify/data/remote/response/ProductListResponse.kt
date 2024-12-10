package com.dicoding.thriftify.data.remote.response

data class ProductListResponse(
    val data: List<Product>
)

data class Product(
    val id: String,
    val name: String,
    val price: Int,
    val img: String,
    val description: String,
    val sellerEmail: String,
    val imageUrl: String,
    val sellerPhone: String
)

