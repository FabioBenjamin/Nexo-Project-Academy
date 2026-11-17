package com.nexo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.nexo.security.TokenManager
import com.nexo.ui.auth.LoginScreen
import com.nexo.ui.auth.LoginViewModel
import com.nexo.ui.auth.RegisterScreen
import com.nexo.ui.auth.RegisterViewModel
import com.nexo.ui.home.HomeScreen
import com.nexo.ui.home.HomeViewModel
import com.nexo.ui.profile.AchievementsScreen
import com.nexo.ui.profile.EditProfileScreen
import com.nexo.ui.profile.EditProfileViewModel
import com.nexo.ui.profile.ProfileScreen
import com.nexo.ui.profile.ProfileViewModel
import com.nexo.ui.settings.SettingsScreen
import com.nexo.ui.settings.SettingsViewModel
import com.nexo.ui.splash.SplashScreen
import com.nexo.ui.splash.SplashViewModel
import com.nexo.ui.test.StudyTestScreen
import com.nexo.ui.test.StudyTestViewModel
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
    const val STUDY_TEST = "study_test/{testId}/{passingScore}"
    const val PROFILE = "profile"
    const val ACHIEVEMENTS = "achievements"
    const val SETTINGS = "settings"
    const val EDIT_PROFILE = "edit_profile?username={username}&email={email}"

    fun studyTestRoute(testId: Long, passingScore: Int) = "study_test/$testId/$passingScore"
}

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
        // Rota: Splash Screen
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

        // Rota: Login Screen
        composable(Routes.LOGIN) {
            val userApiService = RetrofitClient.getUserApiService(context)
            val loginViewModel = viewModel { LoginViewModel(authApiService, userApiService, tokenManager) }
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

        // Rota: Register Screen
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

        // Rota: Home Screen (Com refresh automático na entrada/retorno)
        composable(Routes.HOME) {
            val trailApiService = RetrofitClient.getTrailApiService(context)
            val userApiService = RetrofitClient.getUserApiService(context)
            val homeViewModel = viewModel {
                HomeViewModel(trailApiService, userApiService, authApiService, tokenManager)
            }

            LaunchedEffect(Unit) {
                homeViewModel.loadTrails()
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
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBottom = handleBottomNavigation
            )
        }

        // Rota: Create Trail Screen
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

        // Rota: Trail Details Screen
        composable(
            route = Routes.TRAIL_DETAILS,
            arguments = listOf(navArgument("trailId") { type = NavType.LongType })
        ) { backStackEntry ->
            val trailId = backStackEntry.arguments?.getLong("trailId") ?: 0L
            val trailApiService = RetrofitClient.getTrailApiService(context)
            val userApiService = RetrofitClient.getUserApiService(context)
            val achievementApiService = RetrofitClient.getAchievementApiService(context)
            val trailDetailsViewModel = viewModel {
                TrailDetailsViewModel(
                    trailApiService = trailApiService,
                    userApiService = userApiService,
                    achievementApiService = achievementApiService,
                    tokenManager = tokenManager,
                    trailId = trailId
                )
            }

            LaunchedEffect(Unit) {
                trailDetailsViewModel.loadTrailDetails()
            }

            TrailDetailsScreen(
                viewModel = trailDetailsViewModel,
                onBackClick = { navController.popBackStack() },
                onStartTest = { testId, passingScore ->
                    navController.navigate(Routes.studyTestRoute(testId, passingScore))
                }
            )
        }

        // Rota: Study Test Screen
        composable(
            route = Routes.STUDY_TEST,
            arguments = listOf(
                navArgument("testId") { type = NavType.LongType },
                navArgument("passingScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val testId = backStackEntry.arguments?.getLong("testId") ?: 0L
            val passingScore = backStackEntry.arguments?.getInt("passingScore") ?: 0
            val studyTestApiService = RetrofitClient.getStudyTestApiService(context)
            val userApiService = RetrofitClient.getUserApiService(context)

            val studyTestViewModel = viewModel {
                StudyTestViewModel(
                    testId = testId,
                    passingScore = passingScore,
                    studyTestApiService = studyTestApiService,
                    userApiService = userApiService,
                    tokenManager = tokenManager
                )
            }

            StudyTestScreen(
                viewModel = studyTestViewModel,
                onBackClick = { navController.popBackStack() },
                onUnauthorized = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Rota: Profile Screen
        composable(Routes.PROFILE) {
            val userApiService = RetrofitClient.getUserApiService(context)
            val profileViewModel = viewModel { ProfileViewModel(userApiService, tokenManager) }

            LaunchedEffect(Unit) {
                profileViewModel.loadProfile()
            }

            ProfileScreen(
                viewModel = profileViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToEditProfile = { username, email ->
                    navController.navigate("edit_profile?username=$username&email=$email")
                },
                onNavigateToAchievements = {
                    navController.navigate(Routes.ACHIEVEMENTS)
                },
                onNavigateToSettings = {
                    handleBottomNavigation(Routes.SETTINGS)
                },
                onNavigateBottom = handleBottomNavigation
            )
        }

        // Rota: Achievements Screen (Conquistas & Recordes Pessoais)
        composable(Routes.ACHIEVEMENTS) {
            val userApiService = RetrofitClient.getUserApiService(context)
            val profileViewModel = viewModel { ProfileViewModel(userApiService, tokenManager) }

            AchievementsScreen(
                viewModel = profileViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Rota: Settings Screen
        composable(Routes.SETTINGS) {
            val settingsApiService = RetrofitClient.getClient(context).create(SettingsApiService::class.java)
            val settingsViewModel = viewModel { SettingsViewModel(settingsApiService, tokenManager) }

            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToEditProfile = { username, email ->
                    navController.navigate("edit_profile?username=$username&email=$email")
                },
                onNavigateBottom = handleBottomNavigation
            )
        }

        // Rota: Edit Profile Screen
        composable(
            route = Routes.EDIT_PROFILE,
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val userApiService = RetrofitClient.getUserApiService(context)

            val editProfileViewModel = viewModel {
                EditProfileViewModel(
                    userApiService = userApiService,
                    authApiService = authApiService,
                    tokenManager = tokenManager,
                    initialUsername = username,
                    initialEmail = email
                )
            }

            EditProfileScreen(
                viewModel = editProfileViewModel,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                },
                onLogoutOrDeleteSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}