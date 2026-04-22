package com.task.sm.bitcot.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.sm.bitcot.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["productId"])

    private val _uiState = MutableStateFlow(ProductDetailUiState(isLoading = true))
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "ProductDetailViewModel initialized for productId=$productId")
        fetchProduct()
    }

    fun fetchProduct() {
        viewModelScope.launch {
            Log.d(TAG, "fetchProduct started for productId=$productId")
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                productRepository.getProductDetail(productId)
            }.onSuccess { product ->
                Log.d(TAG, "fetchProduct success: ${product.title} (id=${product.id})")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        product = product
                    )
                }
            }.onFailure { throwable ->
                Log.e(TAG, "fetchProduct failed for productId=$productId: ${throwable.message}", throwable)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load product detail."
                    )
                }
            }
        }
    }

    private companion object {
        private const val TAG = "ProductDetailVM"
    }
}
