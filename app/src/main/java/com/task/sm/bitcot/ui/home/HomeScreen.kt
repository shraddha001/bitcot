package com.task.sm.bitcot.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.task.sm.bitcot.R
import com.task.sm.bitcot.domain.model.Product
import com.task.sm.bitcot.ui.components.stockStatus
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    onProductClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onRetry = viewModel::fetchProducts,
        onCategorySelected = viewModel::onCategorySelected,
        onProductClick = onProductClick
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    onRetry: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onProductClick: (Int) -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.errorMessage != null -> {
            ErrorState(message = uiState.errorMessage, onRetry = onRetry)
        }

        else -> {
            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()
            val showScrollToTop by remember {
                derivedStateOf {
                    listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 240
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Discover Products",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Browse by category and find great deals",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Categories",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.categories.forEach { category ->
                                val selected = category == uiState.selectedCategory
                                AssistChip(
                                    onClick = { onCategorySelected(category) },
                                    label = {
                                        Text(
                                            text = category.replaceFirstChar { it.uppercase() },
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (selected) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        labelColor = if (selected) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                )
                            }
                        }
                    }
                    items(uiState.filteredProducts, key = { it.id }) { product ->
                        ProductItem(product = product, onClick = { onProductClick(product.id) })
                    }
                }

                AnimatedVisibility(
                    visible = showScrollToTop,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Scroll to top"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductItem(
    product: Product,
    onClick: () -> Unit
) {
    val stock = stockStatus(product.stock)
    val originalPrice = product.price.toDouble()
    val discountedPrice = originalPrice * (1 - (product.discountPercentage.toDouble() / 100))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box {
                SubcomposeAsyncImage(
                    model = product.thumbnail,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(14.dp)),

                    loading = {
                        Image(
                            painter = painterResource(R.drawable.product_placeholder), // your placeholder image
                            contentDescription = "Loading",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    },

                    error = {
                        Image(
                            painter = painterResource(R.drawable.product_placeholder), // fallback image
                            contentDescription = "Error",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                )
               /* SubcomposeAsyncImage(
                    model = product.thumbnail,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
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
                                text = "No image",
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                )*/
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${product.discountPercentage.toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$${formatCurrency(discountedPrice)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${formatCurrency(originalPrice)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Rating ${formatRating(product.rating)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(stock.color)
                    )
                    Text(
                        text = "Stock ${product.stock} - ${stock.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = stock.color
                    )
                }
            }
        }
    }
}

private fun formatCurrency(price: Double): String {
    return String.format(Locale.US, "%.2f", price)
}

private fun formatRating(rating: Number): String {
    return String.format(Locale.US, "%.1f", rating.toDouble())
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
        TextButton(modifier = Modifier.height(44.dp), onClick = onRetry) {
            Text("Retry")
        }
    }
}
