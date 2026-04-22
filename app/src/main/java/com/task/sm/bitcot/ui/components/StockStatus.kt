package com.task.sm.bitcot.ui.components

import androidx.compose.ui.graphics.Color

data class StockStatusUi(
    val label: String,
    val color: Color
)

fun stockStatus(stock: Int): StockStatusUi {
    return when {
        stock > 50 -> StockStatusUi("Available", Color(0xFF2E7D32))
        stock in 1..50 -> StockStatusUi("Limited", Color(0xFFEF6C00))
        else -> StockStatusUi("Unavailable", Color(0xFFC62828))
    }
}
