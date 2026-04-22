package com.task.sm.bitcot.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.task.sm.bitcot.domain.model.Product
import com.task.sm.bitcot.ui.components.stockStatus

@Composable
fun ProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ProductDetailScreen(
        uiState = uiState,
        onRetry = viewModel::fetchProduct
    )
}

@Composable
private fun ProductDetailScreen(
    uiState: ProductDetailUiState,
    onRetry: () -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.errorMessage != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(uiState.errorMessage, style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }

        uiState.product != null -> {
            ProductDetailContent(product = uiState.product)
        }
    }
}

@Composable
private fun ProductDetailContent(product: Product) {
    val pagerState = rememberPagerState(pageCount = { product.images.size })
    val stock = stockStatus(product.stock)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) { page ->
            SubcomposeAsyncImage(
                model = product.images[page],
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Image unavailable",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(product.images.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (pagerState.currentPage == index) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            }
                        )
                )
            }
        }

        Text(product.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(product.description, style = MaterialTheme.typography.bodyMedium)
        Text("Price: $${product.price}", style = MaterialTheme.typography.titleMedium)
        Text("Discount: ${product.discountPercentage}%", style = MaterialTheme.typography.bodyMedium)
        Text("Rating: ${product.rating}", style = MaterialTheme.typography.bodyMedium)
        Text("Stock: ${product.stock} (${stock.label})", style = MaterialTheme.typography.bodyMedium, color = stock.color)
        Text("Brand: ${product.brand}", style = MaterialTheme.typography.bodyMedium)
        Text("Category: ${product.category}", style = MaterialTheme.typography.bodyMedium)
    }
}
