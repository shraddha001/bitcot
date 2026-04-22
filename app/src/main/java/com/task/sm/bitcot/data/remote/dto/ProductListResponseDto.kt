package com.task.sm.bitcot.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductListResponseDto(
    val statusCode: Int = 0,
    val data: ProductListDataDto = ProductListDataDto(),
    val message: String = "",
    val success: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ProductListDataDto(
    val page: Int = 1,
    val limit: Int = 0,
    val totalPages: Int = 0,
    val previousPage: Boolean = false,
    val nextPage: Boolean = false,
    val totalItems: Int = 0,
    val currentPageItems: Int = 0,
    val data: List<ProductDto> = emptyList()
)
