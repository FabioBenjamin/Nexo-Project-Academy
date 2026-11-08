/* package com.nexo.ui.trail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexo.dto.TopicResponse
import com.nexo.ui.home.StatusBadge
import com.nexo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailDetailsScreen(
    viewModel: TrailDetailsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalhes da Trilha",
                        style = MaterialTheme.typography.headlineSmall,
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
        containerColor = CremeSuave
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            when (val state = uiState) {
                is TrailDetailsUiState.Loading -> {
                    CircularProgressIndicator(
                        color = RoxoAcolhedor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is TrailDetailsUiState.Error -> {
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
                            onClick = { viewModel.loadTrailDetails() },
                            colors = ButtonDefaults.buttonColors(containerColor = RoxoAcolhedor)
                        ) {
                            Text("Tentar Novamente", color = BrancoPuro)
                        }
                    }
                }

                is TrailDetailsUiState.Success -> {
                    val trail = state.trail
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Card principal com resumo da trilha
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = BrancoPuro),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = trail.title,
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = TextoPrincipal,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        StatusBadge(status = trail.status)
                                    }

                                    if (!trail.description.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = trail.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextoSecundario
                                        )
                                    }

                                    if (!trail.targetDate.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Meta de conclusão: ${trail.targetDate}",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = RoxoAcolhedor
                                        )
                                    }
                                }
                            }
                        }

                        // Título da seção de tópicos
                        item {
                            Text(
                                text = "Tópicos de Estudo (${trail.topics.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Lista de tópicos e subtópicos
                        items(trail.topics, key = { it.id }) { topic ->
                            TopicCard(topic = topic)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopicCard(topic: TopicResponse) {
    val isCompleted = topic.status.equals("COMPLETED", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = topic.status,
                    tint = if (isCompleted) VerdeEquilibrio else RoxoAcolhedor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoPrincipal,
                    modifier = Modifier.weight(1f)
                )
            }

            if (!topic.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = topic.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    modifier = Modifier.padding(start = 32.dp)
                )
            }

            if (!topic.targetDate.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Previsto para: ${topic.targetDate}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoSecundario,
                    modifier = Modifier.padding(start = 32.dp)
                )
            }

            // Subtópicos (caso o Gemini tenha gerado sub-níveis)
            if (topic.subTopics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CremeSuave.copy(alpha = 0.6f))
                        .padding(12.dp)
                ) {
                    topic.subTopics.forEach { subTopic ->
                        val subDone = subTopic.status.equals("COMPLETED", ignoreCase = true)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (subDone) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                contentDescription = subTopic.status,
                                tint = if (subDone) VerdeEquilibrio else TextoSecundario,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = subTopic.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoPrincipal
                            )
                        }
                    }
                }
            }
        }
    }
} */