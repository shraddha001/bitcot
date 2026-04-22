package com.task.sm.bitcot.data.repository

import com.task.sm.bitcot.data.mapper.toDomain
import com.task.sm.bitcot.data.remote.api.ProductApiService
import com.task.sm.bitcot.domain.model.Product
import com.task.sm.bitcot.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductApiService
) : ProductRepository {

    // In-memory cache for basic resilience and faster detail reopening.
    private var productCache: List<Product> = emptyList()

    override fun getCachedProducts(): List<Product> = productCache

    override suspend fun getProducts(page: Int, limit: Int): List<Product> {
        val response = apiService.getProducts(page = page, limit = limit)
        val products = response.data.data.map { it.toDomain() }
        productCache = products
        return products
    }

    override suspend fun getProductDetail(id: Int): Product {
        val cached = productCache.firstOrNull { it.id == id }
        if (cached != null) return cached

        return apiService.getProductDetail(id).data.toDomain()
    }
}
