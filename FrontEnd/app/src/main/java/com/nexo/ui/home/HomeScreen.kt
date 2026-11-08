package com.nexo.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.R
import com.nexo.dto.StudyTrailSummaryResponse
import com.nexo.ui.navigation.NexoBottomBar
import com.nexo.ui.navigation.Routes
import com.nexo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onCreateTrailClick: () -> Unit,
    onTrailClick: (Long) -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.mascote_default),
                            contentDescription = "Nexo TopBar Icon",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Nexo Academy",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextoPrincipal
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = RoxoAcolhedor)
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = TextoPrincipal)
                    }
                    IconButton(onClick = { viewModel.logout(onLogoutClick) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sair", tint = VermelhoErro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CremeSuave)
            )
        },
        bottomBar = {
            NexoBottomBar(
                currentRoute = Routes.HOME,
                onNavigate = onNavigateBottom
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTrailClick,
                containerColor = RoxoAcolhedor,
                contentColor = BrancoPuro,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Trilha")
            }
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
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(
                        color = RoxoAcolhedor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = VermelhoErro,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadTrails() },
                            colors = ButtonDefaults.buttonColors(containerColor = RoxoAcolhedor)
                        ) {
                            Text("Tentar Novamente", color = BrancoPuro)
                        }
                    }
                }
                is HomeUiState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            DashboardWelcomeCard(onProfileClick = onProfileClick)
                        }

                        if (state.trails.isEmpty()) {
                            item {
                                EmptyStateCard(onCreateTrailClick = onCreateTrailClick)
                            }
                        } else {
                            item {
                                Text(
                                    text = "Minhas Trilhas Ativas",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextoPrincipal,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            items(state.trails) { trail ->
                                TrailCard(trail = trail, onClick = { onTrailClick(trail.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardWelcomeCard(onProfileClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() }
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(RoxoAcolhedor.copy(alpha = 0.2f))
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = "Foco",
                    tint = RoxoAcolhedor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Olá, Nexo!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextoPrincipal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Continue sua jornada\nde aprendizado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "XP",
                            tint = AmareloAcolhedor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Nível 3",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "200 / 400 XP",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextoSecundario
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = 0.5f,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = AmareloAcolhedor,
                        trackColor = CinzaDesabilitado
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.mascote_focado),
                    contentDescription = "Mascote Focado",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(90.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(onCreateTrailClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(RoxoAcolhedor.copy(alpha = 0.2f))
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = "Missão",
                    tint = RoxoAcolhedor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mascote_apoio),
                    contentDescription = "Mascote Sem Atividades",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(110.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ainda não há atividades por aqui.",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Que tal começar uma nova missão?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onCreateTrailClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoxoAcolhedor,
                        contentColor = BrancoPuro
                    )
                ) {
                    Text(
                        text = "Explorar atividades",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun TrailCard(trail: StudyTrailSummaryResponse, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trail.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!trail.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = trail.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            StatusBadge(status = trail.status)
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (badgeColor, textColor) = when (status.uppercase()) {
        "COMPLETED" -> VerdeEquilibrio to BrancoPuro
        "IN_PROGRESS" -> AmareloAcolhedor to TextoPrincipal
        else -> CinzaDesabilitado to TextoSecundario
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = badgeColor.copy(alpha = 0.2f)
    ) {
        Text(
            text = when (status.uppercase()) {
                "COMPLETED" -> "Concluído"
                "IN_PROGRESS" -> "Em Andamento"
                else -> "Pendente"
            },
            style = MaterialTheme.typography.labelMedium,
            color = badgeColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}