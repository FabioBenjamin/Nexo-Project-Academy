package com.nexo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nexo.ui.navigation.AppNavigation
import com.nexo.ui.theme.NexoAcademyTheme

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