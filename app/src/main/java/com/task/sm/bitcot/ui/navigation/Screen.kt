package com.task.sm.bitcot.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object ProductDetail : Screen("productDetail/{productId}") {
        fun createRoute(productId: Int): String = "productDetail/$productId"
    }
}
