package com.example.ecomapp.model

data class ProductModel(
    val name: String? = null,
    val description: String? = null,
    val mrp: String? = null,
    val sellingPrice: String? = null,
    val id: String? = null,
    val imageUrl: String? = null,
    val coverImageUrl: String? = null,
    val images: List<String>? = null,
    val category: String? = null
)
