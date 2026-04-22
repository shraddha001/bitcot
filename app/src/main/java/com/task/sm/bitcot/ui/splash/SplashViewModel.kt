package com.task.sm.bitcot.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.sm.bitcot.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _navigateToHome = MutableSharedFlow<Unit>()
    val navigateToHome: SharedFlow<Unit> = _navigateToHome.asSharedFlow()

    init {
        preloadAndContinue()
    }

    private fun preloadAndContinue() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            runCatching {
                productRepository.getProducts()
            }
            val elapsed = System.currentTimeMillis() - startTime
            val minSplashDuration = 1200L
            if (elapsed < minSplashDuration) {
                delay(minSplashDuration - elapsed)
            }
            _navigateToHome.emit(Unit)
        }
    }
}
