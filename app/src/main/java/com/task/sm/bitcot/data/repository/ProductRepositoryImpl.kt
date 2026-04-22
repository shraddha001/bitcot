package com.task.sm.bitcot.data.repository

import com.task.sm.bitcot.data.mapper.toDomain
import com.task.sm.bitcot.data.local.ProductCacheDataSource
import com.task.sm.bitcot.data.remote.api.ProductApiService
import com.task.sm.bitcot.core.network.NetworkChecker
import com.task.sm.bitcot.domain.model.Product
import com.task.sm.bitcot.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductApiService,
    private val productCacheDataSource: ProductCacheDataSource,
    private val networkChecker: NetworkChecker
) : ProductRepository {

    // In-memory cache for basic resilience and faster detail reopening.
    private var productCache: List<Product> = productCacheDataSource.getProducts()

    override fun getCachedProducts(): List<Product> = productCache

    override suspend fun getProducts(page: Int, limit: Int): List<Product> {
        if (!networkChecker.isInternetAvailable()) {
            return productCache.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("No internet connection and no cached products available.")
        }

        return runCatching {
            apiService.getProducts(page = page, limit = limit)
        }.map { response ->
            response.data.data.map { it.toDomain() }
        }.onSuccess { products ->
            productCache = products
            productCacheDataSource.saveProducts(products)
        }.getOrElse { throwable ->
            productCache.takeIf { it.isNotEmpty() } ?: throw throwable
        }
    }

    override suspend fun getProductDetail(id: Int): Product {
        val cached = productCache.firstOrNull { it.id == id }
        if (cached != null) return cached
        if (!networkChecker.isInternetAvailable()) {
            throw IllegalStateException("No internet connection and product is not available in cache.")
        }

        return runCatching {
            apiService.getProductDetail(id).data.toDomain()
        }.getOrElse { throwable ->
            productCache.firstOrNull { it.id == id } ?: throw throwable
        }
    }
}
