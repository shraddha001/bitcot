package com.task.sm.bitcot.data.local

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.task.sm.bitcot.domain.model.Product
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductCacheDataSource @Inject constructor(
    @ApplicationContext context: Context,
    moshi: Moshi
) {
    private val sharedPreferences = context.getSharedPreferences(CACHE_PREFS_NAME, Context.MODE_PRIVATE)
    private val productListAdapter = moshi.adapter<List<Product>>(
        Types.newParameterizedType(List::class.java, Product::class.java)
    )

    fun saveProducts(products: List<Product>) {
        val json = productListAdapter.toJson(products)
        sharedPreferences.edit().putString(KEY_PRODUCTS, json).apply()
    }

    fun getProducts(): List<Product> {
        val cachedJson = sharedPreferences.getString(KEY_PRODUCTS, null) ?: return emptyList()
        return productListAdapter.fromJson(cachedJson).orEmpty()
    }

    private companion object {
        private const val CACHE_PREFS_NAME = "bitcot_cache"
        private const val KEY_PRODUCTS = "products"
    }
}
