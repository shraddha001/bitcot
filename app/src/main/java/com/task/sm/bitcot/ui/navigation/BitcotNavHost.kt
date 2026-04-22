package com.task.sm.bitcot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.task.sm.bitcot.ui.detail.ProductDetailRoute
import com.task.sm.bitcot.ui.home.HomeRoute
import com.task.sm.bitcot.ui.splash.SplashRoute

@Composable
fun BitcotNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashRoute(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = Screen.Home.route) {
            HomeRoute(
                onProductClick = { id ->
                    navController.navigate(Screen.ProductDetail.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) {
            ProductDetailRoute(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
