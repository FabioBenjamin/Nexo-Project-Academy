package com.nexo.ui.trail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexo.dto.StudyTrailResponse
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
                    TrailDetailsContent(trail = state.trail)
                }
            }
        }
    }
}

@Composable
fun TrailDetailsContent(trail: StudyTrailResponse) {
    // Calculates completed topics and overall progress percentage
    val allTopicsCount = trail.topics.sumOf { 1 + it.subTopics.size }
    val completedCount = trail.topics.sumOf { topic ->
        val mainCompleted = if (topic.status.equals("COMPLETED", ignoreCase = true)) 1 else 0
        val subCompleted = topic.subTopics.count { it.status.equals("COMPLETED", ignoreCase = true) }
        mainCompleted + subCompleted
    }
    val progressFraction = if (allTopicsCount > 0) completedCount.toFloat() / allTopicsCount else 0f

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Header Card with Trail Info and Progress Bar
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BrancoPuro),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = trail.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextoPrincipal,
                            modifier = Modifier.weight(1f)
                        )
                        StatusBadge(status = trail.status)
                    }
                    if (!trail.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = trail.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Prazo",
                            tint = RoxoAcolhedor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Data Limite: ${trail.targetDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Overall Trail Completion Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progresso Geral",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextoPrincipal
                        )
                        Text(
                            text = "${(progressFraction * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = RoxoAcolhedor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = VerdeEquilibrio,
                        trackColor = CinzaDesabilitado
                    )
                }
            }
        }

        // Topics Section Title
        item {
            Text(
                text = "Tópicos de Estudo",
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipal,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Nested Topics & Subtopics List
        items(trail.topics) { topic ->
            ExpandableTopicCard(topic = topic)
        }
    }
}

@Composable
fun ExpandableTopicCard(topic: TopicResponse) {
    var isExpanded by remember { mutableStateOf(true) }
    val isCompleted = topic.status.equals("COMPLETED", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (topic.subTopics.isNotEmpty()) isExpanded = !isExpanded }
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Status",
                    tint = if (isCompleted) VerdeEquilibrio else TextoSecundario,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoPrincipal
                    )
                    if (!topic.description.isNullOrBlank()) {
                        Text(
                            text = topic.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                    }
                }

                if (topic.subTopics.isNotEmpty()) {
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expandir Subtópicos",
                            tint = TextoSecundario
                        )
                    }
                }
            }

            // Subtopics Tree Indented View
            if (topic.subTopics.isNotEmpty()) {
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .padding(start = 28.dp, top = 12.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        topic.subTopics.forEach { subTopic ->
                            SubTopicItem(subTopic = subTopic)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubTopicItem(subTopic: TopicResponse) {
    val isSubCompleted = subTopic.status.equals("COMPLETED", ignoreCase = true)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = if (isSubCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = "Subtopic Status",
            tint = if (isSubCompleted) VerdeEquilibrio else TextoSecundario,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = subTopic.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
            if (!subTopic.description.isNullOrBlank()) {
                Text(
                    text = subTopic.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
        }
    }
}