package com.example.ecomapp.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(product: ProductModelEntity)

    @Delete
    suspend fun deleteProduct(product: ProductModelEntity)

    @Update
    suspend fun updateProduct(product: ProductModelEntity)

    // Get all products
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductModelEntity>>  // Using Flow for better reactivity

    // Check if product exists (returns count)
    @Query("SELECT COUNT(*) FROM products WHERE productId = :id")
    suspend fun isExist(id: String): Int
}
