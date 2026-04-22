package com.task.sm.bitcot.ui.detail

import com.task.sm.bitcot.domain.model.Product

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val errorMessage: String? = null
)
