package com.example.nexoacademy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.nexoacademy.ui.navigation.AppNavigation
import com.example.nexoacademy.ui.theme.NexoAcademyTheme

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