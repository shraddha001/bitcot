package com.task.sm.bitcot.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.sm.bitcot.domain.model.Product
import com.task.sm.bitcot.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = false))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val cachedProducts = productRepository.getCachedProducts()
        if (cachedProducts.isNotEmpty()) {
            updateProducts(cachedProducts)
        } else {
            fetchProducts()
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                productRepository.getProducts()
            }.onSuccess { products ->
                updateProducts(products)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Something went wrong while fetching products."
                    )
                }
            }
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    private fun updateProducts(products: List<Product>) {
        val categories = products.map { it.category }.distinct().sorted()
        _uiState.update {
            it.copy(
                isLoading = false,
                products = products,
                categories = listOf(HomeUiState.ALL_CATEGORY) + categories,
                selectedCategory = HomeUiState.ALL_CATEGORY,
                errorMessage = null
            )
        }
    }
}
