package com.nexo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.nexo.ui.profile.LearningProfileQuizScreen
import com.nexo.ui.profile.LearningProfileViewModel
import com.nexo.ui.profile.ProfileScreen
import com.nexo.ui.profile.ProfileViewModel
import com.nexo.ui.profile.QuizWelcomeScreen
import com.nexo.ui.settings.SettingsScreen
import com.nexo.ui.settings.SettingsViewModel
import com.nexo.ui.splash.SplashScreen
import com.nexo.ui.splash.SplashViewModel
import com.nexo.ui.test.StudyTestScreen
import com.nexo.ui.test.StudyTestViewModel
import com.nexo.ui.theme.AppPreferencesState
import com.nexo.ui.trail.CreateTrailScreen
import com.nexo.ui.trail.CreateTrailViewModel
import com.nexo.ui.trail.TrailDetailsScreen
import com.nexo.ui.trail.TrailDetailsViewModel
import kotlinx.coroutines.launch

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val QUIZ_WELCOME = "quiz_welcome"
    const val LEARNING_PROFILE_QUIZ = "learning_profile_quiz?fromOnboarding={fromOnboarding}"
    const val HOME = "home"
    const val CREATE_TRAIL = "create_trail"
    const val TRAIL_DETAILS = "trail_details/{trailId}"
    const val STUDY_TEST = "study_test/{testId}/{passingScore}"
    const val PROFILE = "profile"
    const val ACHIEVEMENTS = "achievements"
    const val SETTINGS = "settings"
    const val EDIT_PROFILE = "edit_profile?username={username}&email={email}"

    fun studyTestRoute(testId: Long, passingScore: Int) = "study_test/$testId/$passingScore"
    fun learningQuizRoute(fromOnboarding: Boolean) = "learning_profile_quiz?fromOnboarding=$fromOnboarding"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val authApiService = RetrofitClient.getAuthApiService(context)
    val tokenManager = TokenManager(context)
    val coroutineScope = rememberCoroutineScope()

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
            val settingsApiService = RetrofitClient.getClient(context).create(SettingsApiService::class.java)
            val loginViewModel = viewModel { LoginViewModel(authApiService, userApiService, tokenManager) }
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToSplash = {navController.popBackStack()},
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    coroutineScope.launch {
                        val localTheme = tokenManager.getTheme() ?: "system"
                        val localLanguage = tokenManager.getLanguage() ?: "system"

                        val settingsResponse = try {
                            settingsApiService.getSettings().body()
                        } catch (_: Exception) {
                            null
                        }
                        if (settingsResponse != null) {
                            val needsSync = (localTheme == "system" && settingsResponse.theme != "system") ||
                                    (localLanguage == "system" && settingsResponse.language != "system")

                            val finalSettings = if (needsSync) {
                                val updated = settingsResponse.copy(
                                    theme = localTheme,
                                    language = localLanguage
                                )
                                try {
                                    settingsApiService.updateSettings(updated)
                                } catch (_: Exception) { }
                                updated
                            } else {
                                settingsResponse
                            }

                            AppPreferencesState.updateFromSettings(finalSettings.theme, finalSettings.language)
                            tokenManager.saveCachedSettings(finalSettings)
                        }

                        val nextRoute = if (settingsResponse != null && !settingsResponse.hasSeenOnboardingPrompt) {
                            Routes.QUIZ_WELCOME
                        } else {
                            Routes.HOME
                        }

                        navController.navigate(nextRoute) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
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

        // Rota: Quiz Welcome Screen (Optional Post-Login Invitation)
        composable(Routes.QUIZ_WELCOME) {
            val settingsApiService = RetrofitClient.getClient(context).create(SettingsApiService::class.java)
            val learningProfileViewModel = viewModel { LearningProfileViewModel(settingsApiService) }

            QuizWelcomeScreen(
                onStartQuiz = {
                    navController.navigate(Routes.learningQuizRoute(fromOnboarding = true)) {
                        popUpTo(Routes.QUIZ_WELCOME) { inclusive = true }
                    }
                },
                onSkipQuiz = {
                    learningProfileViewModel.skipQuiz {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.QUIZ_WELCOME) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Rota: Learning Profile Quiz Screen
        composable(
            route = Routes.LEARNING_PROFILE_QUIZ,
            arguments = listOf(
                navArgument("fromOnboarding") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val fromOnboarding = backStackEntry.arguments?.getBoolean("fromOnboarding") ?: false
            val settingsApiService = RetrofitClient.getClient(context).create(SettingsApiService::class.java)
            val learningProfileViewModel = viewModel { LearningProfileViewModel(settingsApiService) }
            val uiState by learningProfileViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isCompleted) {
                if (uiState.isCompleted) {
                    if (fromOnboarding) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LEARNING_PROFILE_QUIZ) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            }

            LearningProfileQuizScreen(
                uiState = uiState,
                onSelectSingleOption = learningProfileViewModel::onSelectSingleOption,
                onToggleMultipleOption = learningProfileViewModel::onToggleMultipleOption,
                onUpdateTextAnswer = learningProfileViewModel::onUpdateTextAnswer,
                onNextStep = learningProfileViewModel::onNextStep,
                onPreviousStep = learningProfileViewModel::onPreviousStep,
                onSubmitQuiz = learningProfileViewModel::submitProfileQuiz,
                onNavigateBack = {
                    if (fromOnboarding) {
                        learningProfileViewModel.skipQuiz {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LEARNING_PROFILE_QUIZ) { inclusive = true }
                            }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        // Rota: Home Screen
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

        // Rota: Achievements Screen
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

            LaunchedEffect(Unit) {
                settingsViewModel.loadSettings()
            }

            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToEditProfile = { username, email ->
                    navController.navigate("edit_profile?username=$username&email=$email")
                },
                onNavigateToLearningQuiz = {
                    navController.navigate(Routes.learningQuizRoute(fromOnboarding = false))
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