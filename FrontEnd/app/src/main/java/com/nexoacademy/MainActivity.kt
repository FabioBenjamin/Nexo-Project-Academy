package com.nexoacademy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nexoacademy.ui.navigation.AppNavigation
import com.nexoacademy.ui.theme.NexoAcademyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NexoAcademyTheme {
                AppNavigation()
            }
        }
    }
}