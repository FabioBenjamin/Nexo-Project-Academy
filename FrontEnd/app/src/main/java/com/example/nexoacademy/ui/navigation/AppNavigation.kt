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


// Rotas da Parte 1
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CREATE_TRAIL = "create_trail"
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
        startDestination = Routes.LOGIN
    ) {
        // Rota de Login
        composable(Routes.LOGIN) {
            val loginViewModel = viewModel { LoginViewModel(authApiService, tokenManager) }
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    // Navega para a Home e remove o Login da pilha de navegação
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
                    // Após cadastrar com sucesso, redireciona para o Login
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // Rota de Home (Dashboard) -
        composable(Routes.HOME) {
            val trailApiService = RetrofitClient.getTrailApiService(context)
            val homeViewModel = viewModel { HomeViewModel(trailApiService, authApiService, tokenManager) }

            HomeScreen(
                viewModel = homeViewModel,
                onCreateTrailClick = { navController.navigate(Routes.CREATE_TRAIL) },
                onTrailClick = { trailId ->
                    // Direciona para detalhes da trilha (Parte 2)
                },
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // Rota de Criação de Trilha com IA - Vamos implementar em seguida
        composable(Routes.CREATE_TRAIL) {
            // Placeholder temporário enquanto criamos a CreateTrailScreen
        }
    }
}