package com.nexo.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexo.dto.StudyTrailSummaryResponse
import com.nexo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onCreateTrailClick: () -> Unit,
    onTrailClick: (Long) -> Unit,
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Monitors expired session
    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.Error && (uiState as HomeUiState.Error).message.contains("Sessão expirada")) {
            viewModel.logout(onLogoutClick)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Minhas Trilhas", style = MaterialTheme.typography.headlineMedium, color = TextoPrincipal) },
                actions = {
                    IconButton(onClick = { viewModel.logout(onLogoutClick) }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Sair", tint = VermelhoErro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CremeSuave)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTrailClick,
                containerColor = RoxoAcolhedor,
                contentColor = BrancoPuro,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nova Trilha")
            }
        },
        containerColor = CremeSuave
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp)) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(color = RoxoAcolhedor, modifier = Modifier.align(Alignment.Center))
                }
                is HomeUiState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, style = MaterialTheme.typography.bodyLarge, color = VermelhoErro)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadTrails() }, colors = ButtonDefaults.buttonColors(containerColor = RoxoAcolhedor)) {
                            Text("Tentar Novamente", color = BrancoPuro)
                        }
                    }
                }
                is HomeUiState.Success -> {
                    if (state.trails.isEmpty()) {
                        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Nenhuma trilha encontrada", style = MaterialTheme.typography.titleLarge, color = TextoPrincipal)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Clique no botão '+' abaixo para criar sua primeira trilha com a IA!", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, modifier = Modifier.padding(horizontal = 32.dp))
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(vertical = 16.dp), modifier = Modifier.fillMaxSize()) {
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
fun TrailCard(trail: StudyTrailSummaryResponse, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BrancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = trail.title, style = MaterialTheme.typography.titleLarge, color = TextoPrincipal, modifier = Modifier.weight(1f))
                StatusBadge(status = trail.status)
            }
            if (!trail.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = trail.description, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, maxLines = 2)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "COMPLETED" -> Triple(VerdeEquilibrio, BrancoPuro, "Concluída")
        "IN_PROGRESS" -> Triple(AmareloAcolhedor, TextoPrincipal, "Em Andamento")
        else -> Triple(CinzaDesabilitado, TextoPrincipal, "Pendente")
    }
    Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(bgColor).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = textColor)
    }
}