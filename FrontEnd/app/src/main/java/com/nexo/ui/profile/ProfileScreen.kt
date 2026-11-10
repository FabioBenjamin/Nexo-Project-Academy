package com.nexo.ui.profile

import com.nexo.ui.navigation.Routes
import com.nexo.ui.navigation.NexoBottomBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nexo.dto.AchievementDTO
import com.nexo.dto.UserProfileResponse
import com.nexo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meu Perfil",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoPrincipal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TextoPrincipal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CremeSuave)
            )
        },
        bottomBar = {
            NexoBottomBar(
                currentRoute = Routes.PROFILE,
                onNavigate = onNavigateBottom
            )
        },
        containerColor = CremeSuave
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(
                        color = RoxoAcolhedor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ProfileUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = VermelhoErro
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadProfile() },
                            colors = ButtonDefaults.buttonColors(containerColor = RoxoAcolhedor)
                        ) {
                            Text("Tentar Novamente", color = BrancoPuro)
                        }
                    }
                }
                is ProfileUiState.Success -> {
                    ProfileContent(profile = state.profile)
                }
            }
        }
    }
}

@Composable
fun ProfileContent(profile: UserProfileResponse) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Main User Profile Card with XP Level Progress
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BrancoPuro),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // User Avatar Circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(RoxoAcolhedor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.username.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = BrancoPuro
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = profile.username,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextoPrincipal
                )

                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Level and XP indicator row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "XP",
                            tint = AmareloAcolhedor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Nível ${profile.level}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                    }

                    Text(
                        text = "${profile.currentXp} / ${profile.maxXp} XP",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Linear progress bar for experience points
                val progressValue = if (profile.maxXp > 0) profile.currentXp.toFloat() / profile.maxXp else 0f
                LinearProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = AmareloAcolhedor,
                    trackColor = CinzaDesabilitado,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Achievements grid section
        Text(
            text = "Conquistas",
            style = MaterialTheme.typography.titleLarge,
            color = TextoPrincipal,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(profile.achievements) { achievement ->
                AchievementCard(achievement = achievement)
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: AchievementDTO) {
    val cardBg = if (achievement.isUnlocked) BrancoPuro else CinzaDesabilitado.copy(alpha = 0.5f)
    val iconColor = if (achievement.isUnlocked) AmareloAcolhedor else TextoSecundario

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (achievement.isUnlocked) 2.dp else 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = achievement.title,
                tint = iconColor,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = achievement.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = if (achievement.isUnlocked) TextoPrincipal else TextoSecundario
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                color = TextoSecundario
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "+${achievement.xpReward} XP",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (achievement.isUnlocked) VerdeEquilibrio else TextoSecundario
            )
        }
    }
}