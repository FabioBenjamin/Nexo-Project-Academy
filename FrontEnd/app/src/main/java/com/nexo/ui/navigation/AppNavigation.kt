package com.nexo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexo.config.RetrofitClient
import com.nexo.data.api.SettingsApiService
import com.nexo.data.api.UserApiService
import com.nexo.security.TokenManager
import com.nexo.ui.auth.LoginScreen
import com.nexo.ui.auth.LoginViewModel
import com.nexo.ui.auth.RegisterScreen
import com.nexo.ui.auth.RegisterViewModel
import com.nexo.ui.home.HomeScreen
import com.nexo.ui.home.HomeViewModel
import com.nexo.ui.profile.EditProfileScreen
import com.nexo.ui.profile.EditProfileViewModel
import com.nexo.ui.profile.ProfileScreen
import com.nexo.ui.profile.ProfileViewModel
import com.nexo.ui.settings.SettingsScreen
import com.nexo.ui.settings.SettingsViewModel
import com.nexo.ui.splash.SplashScreen
import com.nexo.ui.splash.SplashViewModel
import com.nexo.ui.trail.CreateTrailScreen
import com.nexo.ui.trail.CreateTrailViewModel
import com.nexo.ui.trail.TrailDetailsScreen
import com.nexo.ui.trail.TrailDetailsViewModel

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CREATE_TRAIL = "create_trail"
    const val TRAIL_DETAILS = "trail_details/{trailId}"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    const val EDIT_PROFILE = "edit_profile"
}

@RequiresApi(Build.VERSION_CODES.O)
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val authApiService = RetrofitClient.getAuthApiService(context)
    val tokenManager = TokenManager(context)

    val handleBottomNavigation: (String) -> Unit = { route ->
        if (navController.currentBackStackEntry?.destination?.route != route) {
            navController.navigate(route) {
                popUpTo(Routes.HOME) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // Splash Screen Route
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

        // Login Screen Route
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

        // Register Screen Route
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

        // Home Screen Route
        composable(Routes.HOME) {
            val trailApiService = RetrofitClient.getTrailApiService(context)
            val userApiService = RetrofitClient.getClient(context).create(UserApiService::class.java)
            val homeViewModel = viewModel {
                HomeViewModel(trailApiService, authApiService, userApiService, tokenManager)
            }

            HomeScreen(
                viewModel = homeViewModel,
                onCreateTrailClick = { navController.navigate(Routes.CREATE_TRAIL) },
                onTrailClick = { trailId ->
                    navController.navigate("trail_details/$trailId")
                },
                onProfileClick = { handleBottomNavigation(Routes.PROFILE) },
                onSettingsClick = { handleBottomNavigation(Routes.SETTINGS) },
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateBottom = handleBottomNavigation
            )
        }

        // Create Trail Screen Route
        composable(Routes.CREATE_TRAIL) {
            val trailApiService = RetrofitClient.getTrailApiService(context)
            val createTrailViewModel = viewModel {
                CreateTrailViewModel(trailApiService)
            }

            CreateTrailScreen(
                viewModel = createTrailViewModel,
                onBackClick = { navController.popBackStack() },
                onTrailCreatedSuccessfully = {
                    navController.popBackStack()
                }
            )
        }

        // Trail Details Screen Route
        composable(
            route = Routes.TRAIL_DETAILS,
            arguments = listOf(navArgument("trailId") { type = NavType.LongType })
        ) { backStackEntry ->
            val trailId = backStackEntry.arguments?.getLong("trailId") ?: 0L
            val trailApiService = RetrofitClient.getTrailApiService(context)
            val achievementApiService = RetrofitClient.getAchievementApiService(context)

            val trailDetailsViewModel = viewModel {
                TrailDetailsViewModel(
                    trailApiService = trailApiService,
                    achievementApiService = achievementApiService,
                    tokenManager = tokenManager,
                    trailId = trailId
                )
            }

            TrailDetailsScreen(
                viewModel = trailDetailsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Profile Screen Route
        composable(Routes.PROFILE) {
            val userApiService = RetrofitClient.getClient(context).create(UserApiService::class.java)
            val profileViewModel = viewModel { ProfileViewModel(userApiService) }
            ProfileScreen(
                viewModel = profileViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateBottom = handleBottomNavigation
            )
        }

        // Settings Screen Route
        composable(Routes.SETTINGS) {
            val settingsApiService = RetrofitClient.getClient(context).create(SettingsApiService::class.java)
            val settingsViewModel = viewModel { SettingsViewModel(settingsApiService) }

            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateBottom = handleBottomNavigation
            )
        }

        // Edit Profile Screen Route
        composable(Routes.EDIT_PROFILE) {
            val userApiService = RetrofitClient.getClient(context).create(UserApiService::class.java)
            val editViewModel = viewModel {
                EditProfileViewModel(
                    userApiService,
                    initialUsername = "Nexo",
                    initialEmail = "nexo@email.com"
                )
            }

            EditProfileScreen(
                viewModel = editViewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}