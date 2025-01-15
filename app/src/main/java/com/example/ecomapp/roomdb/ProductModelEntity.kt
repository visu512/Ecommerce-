package com.example.ecomapp.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductModelEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "productId")
    val productId: String = "",

    @ColumnInfo(name = "productName")
    val productName: String = "",

    @ColumnInfo(name = "productImage")
    val productImage: String = "",

    @ColumnInfo(name = "productPrice")
    val productPrice: String = "0",

    @ColumnInfo(name = "quantity")
    var quantity: Int = 1
)
