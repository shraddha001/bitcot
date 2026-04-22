package com.task.sm.bitcot.data.mapper

import com.task.sm.bitcot.data.remote.dto.ProductDto
import com.task.sm.bitcot.domain.model.Product

fun ProductDto.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    brand = brand,
    category = category,
    thumbnail = thumbnail,
    images = images
)
