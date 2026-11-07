package com.nexo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nexo.security.TokenManager
import com.nexo.ui.auth.LoginViewModel
import com.nexo.ui.auth.RegisterViewModel
import com.nexo.config.RetrofitClient
import com.nexo.ui.auth.LoginScreen
import com.nexo.ui.auth.RegisterScreen
import com.nexo.ui.home.HomeScreen
import com.nexo.ui.home.HomeViewModel
import com.nexo.ui.splash.SplashScreen
import com.nexo.ui.splash.SplashViewModel
import com.nexo.ui.trail.CreateTrailScreen
import com.nexo.ui.trail.CreateTrailViewModel

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CREATE_TRAIL = "create_trail"
    const val TRAIL_DETAILS = "trail_details/{trailId}" // Rota futura para a próxima tela
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val authApiService = RetrofitClient.getAuthApiService(context)
    val tokenManager = TokenManager(context)

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH // Set Splash as the initial screen
    ) {
        // Rota Splash
        composable(Routes.SPLASH) {
            val splashViewModel = viewModel { SplashViewModel(tokenManager) }
            SplashScreen(
                viewModel = splashViewModel,
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Login route
        composable(Routes.LOGIN) {
            val loginViewModel = viewModel { LoginViewModel(authApiService, tokenManager) }
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Registration route
        composable(Routes.REGISTER) {
            val registerViewModel = viewModel { RegisterViewModel(authApiService) }
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // Home route (Dashboard)
        composable(Routes.HOME) {
            val trailApiService = RetrofitClient.getTrailApiService(context)
            val homeViewModel = viewModel { HomeViewModel(trailApiService, authApiService, tokenManager) }

            HomeScreen(
                viewModel = homeViewModel,
                onCreateTrailClick = { navController.navigate(Routes.CREATE_TRAIL) },
                onTrailClick = { trailId ->
                    navController.navigate("trail_details/$trailId")
                },
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // AI Powered trail creation route
        composable(Routes.CREATE_TRAIL) {
            val trailApiService = RetrofitClient.getTrailApiService(context)
            val createTrailViewModel = viewModel { CreateTrailViewModel(trailApiService) }

            CreateTrailScreen(
                viewModel = createTrailViewModel,
                onBackClick = { navController.popBackStack() },
                onTrailCreatedSuccessfully = {
                    navController.popBackStack()
                }
            )
        }
    }
}