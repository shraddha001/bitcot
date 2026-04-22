package com.task.sm.bitcot.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDetailResponseDto(
    val statusCode: Int,
    val data: ProductDto,
    val message: String,
    val success: Boolean
)
