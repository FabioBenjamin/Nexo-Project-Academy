package com.example.nexoacademy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nexoacademy.data.api.RetrofitClient
import com.example.nexoacademy.data.local.TokenManager
import com.example.nexoacademy.ui.auth.LoginScreen
import com.example.nexoacademy.ui.auth.LoginViewModel
import com.example.nexoacademy.ui.auth.RegisterScreen
import com.example.nexoacademy.ui.auth.RegisterViewModel
import com.example.nexoacademy.ui.home.HomeScreen
import com.example.nexoacademy.ui.home.HomeViewModel
import com.example.nexoacademy.ui.splash.SplashScreen
import com.example.nexoacademy.ui.splash.SplashViewModel
import com.example.nexoacademy.ui.trail.CreateTrailScreen
import com.example.nexoacademy.ui.trail.CreateTrailViewModel

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
        startDestination = Routes.SPLASH // Define Splash como a tela inicial
    ) {
        // Rota Splash
        composable(Routes.SPLASH) {
            val splashViewModel = viewModel { SplashViewModel(authApiService, tokenManager) }
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

        // Rota de Login
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

        // Rota de Cadastro
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

        // Rota de Home (Dashboard)
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

        // Rota de Criação de Trilha com IA
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