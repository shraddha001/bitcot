package com.task.sm.bitcot.ui.home

import android.util.Log
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
            Log.d(TAG, "Loaded ${cachedProducts.size} cached products")
            updateProducts(cachedProducts)
        } else {
            Log.d(TAG, "No cached products, fetching from repository")
            fetchProducts()
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            Log.d(TAG, "fetchProducts started")
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                productRepository.getProducts()
            }.onSuccess { products ->
                Log.d(TAG, "fetchProducts success: ${products.size} products")
                updateProducts(products)
            }.onFailure { throwable ->
                Log.e(TAG, "fetchProducts failed: ${throwable.message}", throwable)
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
        Log.d(TAG, "Category selected: $category")
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

    private companion object {
        private const val TAG = "HomeViewModel"
    }
}
