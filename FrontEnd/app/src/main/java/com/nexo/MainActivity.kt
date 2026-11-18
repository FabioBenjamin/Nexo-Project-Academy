package com.nexo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.nexo.notifications.StreakNotificationScheduler
import com.nexo.security.TokenManager
import com.nexo.ui.navigation.AppNavigation
import com.nexo.ui.theme.AppPreferencesState
import com.nexo.ui.theme.NexoAcademyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)

        lifecycleScope.launch {
            val savedTheme = tokenManager.getTheme()
            val savedLanguage = tokenManager.getLanguage()
            AppPreferencesState.updateFromSettings(savedTheme, savedLanguage)
        }

        StreakNotificationScheduler.scheduleAll(this)
        askNotificationPermissionIfNeeded()

        setContent {
            NexoAcademyTheme {
                AppNavigation()
            }
        }
    }

    private fun askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}