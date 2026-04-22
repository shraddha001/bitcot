package com.task.sm.bitcot.domain.repository

import com.task.sm.bitcot.domain.model.Product

interface ProductRepository {
    fun getCachedProducts(): List<Product>
    suspend fun getProducts(page: Int = 1, limit: Int = 100): List<Product>
    suspend fun getProductDetail(id: Int): Product
}
