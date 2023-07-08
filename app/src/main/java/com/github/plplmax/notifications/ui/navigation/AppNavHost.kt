package com.github.plplmax.notifications.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.plplmax.notifications.MainViewModel
import com.github.plplmax.notifications.ui.login.LoginScreen
import com.github.plplmax.notifications.ui.welcome.WelcomeScreen

@Composable
fun AppNavHost(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val startDestination = when (val destination = viewModel.startDestination) {
        is Routes.Undefined -> return
        else -> destination.route
    }
    NavHost(navController, startDestination, modifier) {
        composable(Routes.Welcome.route) {
            WelcomeScreen {
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.Welcome.route) { inclusive = true }
                }
            }
        }
        composable(Routes.Login.route) { LoginScreen(viewModel) }
    }
}
