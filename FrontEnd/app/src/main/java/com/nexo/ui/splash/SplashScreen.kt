package com.nexo.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.ui.theme.*

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val destination by viewModel.destination.collectAsState()

    // Listed to the navigation decision emitted by the ViewModel
    LaunchedEffect(destination) {
        when (destination) {
            is SplashDestination.GoToHome -> onNavigateToHome()
            is SplashDestination.GoToLogin -> onNavigateToLogin()
            is SplashDestination.Loading -> {} // Remains on the splash screen
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CremeSuave),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title featuring Nexo visual identity
            Text(
                text = "Nexo Academy",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 36.sp,
                color = RoxoAcolhedor,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sua jornada de aprendizado inteligente",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
            Spacer(modifier = Modifier.height(48.dp))

            // Charging indicator
            CircularProgressIndicator(
                color = AmareloAcolhedor,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}