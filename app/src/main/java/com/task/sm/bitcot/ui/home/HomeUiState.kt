package com.task.sm.bitcot.ui.home

import com.task.sm.bitcot.domain.model.Product

data class HomeUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = ALL_CATEGORY,
    val errorMessage: String? = null
) {
    val filteredProducts: List<Product>
        get() = if (selectedCategory == ALL_CATEGORY) {
            products
        } else {
            products.filter { it.category == selectedCategory }
        }

    companion object {
        const val ALL_CATEGORY = "All"
    }
}
