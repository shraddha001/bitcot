package com.task.sm.bitcot.data.remote.dto

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class ProductDtoMoshiAdapter {

    @FromJson
    fun fromJson(raw: RawProductDto): ProductDto {
        return ProductDto(
            id = raw.id,
            title = raw.title,
            description = raw.description,
            price = raw.price,
            discountPercentage = raw.discountPercentage,
            rating = raw.rating,
            stock = raw.stock,
            brand = raw.brand,
            category = raw.category,
            thumbnail = raw.thumbnail.toLoadableImageUrl(),
            images = raw.images.map { it.toLoadableImageUrl() }
        )
    }

    @ToJson
    fun toJson(product: ProductDto): RawProductDto {
        return RawProductDto(
            id = product.id,
            title = product.title,
            description = product.description,
            price = product.price,
            discountPercentage = product.discountPercentage,
            rating = product.rating,
            stock = product.stock,
            brand = product.brand,
            category = product.category,
            thumbnail = product.thumbnail,
            images = product.images
        )
    }
}

data class RawProductDto(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val discountPercentage: Double = 0.0,
    val rating: Double = 0.0,
    val stock: Int = 0,
    val brand: String = "",
    val category: String = "",
    val thumbnail: String = "",
    val images: List<String> = emptyList()
)

private fun String.toLoadableImageUrl(): String {
    if (isBlank()) return FALLBACK_IMAGE_URL
    if (!contains("cdn.dummyjson.com/product-images/")) return this
    return replace(
        oldValue = "https://cdn.dummyjson.com/product-images/",
        newValue = "https://i.dummyjson.com/data/products/"
    )
}

private const val FALLBACK_IMAGE_URL = "https://dummyjson.com/image/400x300"
