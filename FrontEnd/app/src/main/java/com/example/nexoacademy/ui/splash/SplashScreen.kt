package com.example.nexoacademy.ui.splash

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
import com.example.nexoacademy.ui.theme.*

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val destination by viewModel.destination.collectAsState()

    // Escuta a decisão de navegação emitida pelo ViewModel
    LaunchedEffect(destination) {
        when (destination) {
            is SplashDestination.GoToHome -> onNavigateToHome()
            is SplashDestination.GoToLogin -> onNavigateToLogin()
            is SplashDestination.Loading -> { /* Permanece na Splash */ }
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
            // Título com a identidade visual do Nexo
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

            // Indicador de Carregamento
            CircularProgressIndicator(
                color = AmareloAcolhedor,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}