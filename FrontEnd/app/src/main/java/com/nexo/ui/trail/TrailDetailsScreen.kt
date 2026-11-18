package com.nexo.ui.trail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexo.R
import com.nexo.dto.StudyTestPreviewResponse
import com.nexo.dto.StudyTrailResponse
import com.nexo.dto.TopicResponse
import com.nexo.ui.home.StatusBadge
import com.nexo.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailDetailsScreen(
    viewModel: TrailDetailsViewModel,
    onBackClick: () -> Unit,
    onStartTest: (Long, Int) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val rewardEvent by viewModel.rewardEvent.collectAsState()

    val isActionLoading by viewModel.isActionLoading.collectAsState()
    val trailDeleted by viewModel.trailDeleted.collectAsState()
    val actionError by viewModel.actionError.collectAsState()

    var selectedTopicForModal by remember { mutableStateOf<TopicResponse?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(trailDeleted) {
        if (trailDeleted) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.trailDetailsTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = AppStrings.btnBack,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    if (uiState is TrailDetailsUiState.Success) {
                        IconButton(onClick = {
                            viewModel.clearActionError()
                            showEditDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = AppStrings.cdEditTrail,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = {
                            viewModel.clearActionError()
                            showDeleteDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = AppStrings.cdDeleteTrail,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NexoPullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                when (val state = uiState) {
                    is TrailDetailsUiState.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
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
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadTrailDetails() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(AppStrings.btnTryAgain, color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }

                    is TrailDetailsUiState.Success -> {
                        TrailDetailsContent(
                            trail = state.trail,
                            onTopicClick = { topic -> selectedTopicForModal = topic },
                            progressFraction = state.progressFraction,
                            onStartTest = onStartTest
                        )

                        // Modal de Edição da Trilha
                        if (showEditDialog) {
                            EditTrailDialog(
                                initialTitle = state.trail.title,
                                initialDescription = state.trail.description.orEmpty(),
                                isLoading = isActionLoading,
                                errorMessage = actionError,
                                onDismiss = { if (!isActionLoading) showEditDialog = false },
                                onConfirm = { newTitle, newDesc ->
                                    viewModel.updateTrail(newTitle, newDesc) {
                                        showEditDialog = false
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(AppStrings.editTrailSuccessMsg)
                                        }
                                    }
                                }
                            )
                        }
                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { if (!isActionLoading) showDeleteDialog = false },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                title = { Text(AppStrings.deleteTrailTitle) },
                                text = {
                                    Column {
                                        Text(AppStrings.deleteTrailMessage)
                                        if (actionError != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = actionError!!,
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = { viewModel.deleteTrail() },
                                        enabled = !isActionLoading,
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        if (isActionLoading) {
                                            CircularProgressIndicator(
                                                color = MaterialTheme.colorScheme.onError,
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(AppStrings.deleteTrailConfirmBtn, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showDeleteDialog = false },
                                        enabled = !isActionLoading
                                    ) {
                                        Text(AppStrings.btnCancel)
                                    }
                                }
                            )
                        }
                    }
                }

                selectedTopicForModal?.let { topic ->
                    TopicDetailModal(
                        topic = topic,
                        onDismiss = { selectedTopicForModal = null },
                        onCompleteTopic = { targetTopic ->
                            viewModel.completeTopic(targetTopic.id)
                        }
                    )
                }

                rewardEvent?.let { event ->
                    GamificationRewardDialog(
                        event = event,
                        onDismiss = { viewModel.dismissRewardEvent() },
                        onStartUnlockedTest = onStartTest
                    )
                }
            }
        }
    }
}

@Composable
private fun EditTrailDialog(
    initialTitle: String,
    initialDescription: String,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    val hasChanges = title.trim() != initialTitle.trim() || description.trim() != initialDescription.trim()
    val isFormValid = title.isNotBlank() && description.isNotBlank() && hasChanges

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = AppStrings.editTrailTitle,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(AppStrings.editTrailTitleLabel) },
                    singleLine = true,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = nexoTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(AppStrings.editTrailDescLabel) },
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = nexoTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, description) },
                enabled = !isLoading && isFormValid,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(AppStrings.editTrailSaveBtn)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text(AppStrings.btnCancel)
            }
        }
    )
}

@Composable
fun TrailDetailsContent(
    trail: StudyTrailResponse,
    progressFraction: Float,
    onTopicClick: (TopicResponse) -> Unit,
    onStartTest: (Long, Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        StatusBadge(status = trail.status)
                    }

                    if (!trail.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = trail.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = AppStrings.trailDetailsDeadlineLabel,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${AppStrings.trailDetailsDeadlineLabel}${trail.targetDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = AppStrings.trailDetailsOverallProgress,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${(progressFraction * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
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
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = AppStrings.trailDetailsTopicsHeader,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(trail.topics) { topic ->
            ExpandableTopicCard(
                topic = topic,
                onTopicClick = onTopicClick
            )
        }

        item {
            StudyTestSectionCard(
                preview = trail.studyTestPreviewResponse,
                onStartTest = onStartTest
            )
        }
    }
}

@Composable
fun StudyTestSectionCard(
    preview: StudyTestPreviewResponse?,
    onStartTest: (Long, Int) -> Unit
) {
    if (preview == null) return

    val isBlocked = preview.studyTestStatus.equals("BLOCKED", ignoreCase = true)
    val isCompleted = preview.studyTestStatus.equals("COMPLETED", ignoreCase = true)
    val isOpen = preview.studyTestStatus.equals("OPEN", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppStrings.testSectionTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when {
                            isBlocked -> AppStrings.testSectionBlockedDesc
                            isCompleted -> AppStrings.testSectionCompletedDesc(
                                score = preview.userScore ?: 0,
                                passingScore = preview.passingScore
                            )
                            else -> AppStrings.testSectionOpenDesc(passingScore = preview.passingScore)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(
                        id = when {
                            isBlocked -> R.drawable.mascote_apoio
                            isCompleted -> R.drawable.mascote_default
                            else -> R.drawable.mascote_focado
                        }
                    ),
                    contentDescription = AppStrings.testSectionTitle,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isBlocked -> {
                    Button(
                        onClick = {},
                        enabled = false,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.testBtnLocked,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                isOpen -> {
                    Button(
                        onClick = { onStartTest(preview.id, preview.passingScore) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = AppStrings.testBtnStart,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                isCompleted -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.testApprovedChip(preview.userScore ?: 0),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableTopicCard(
    topic: TopicResponse,
    onTopicClick: (TopicResponse) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val isCompleted = topic.status.equals("COMPLETED", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = topic.title,
                    tint = if (isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onTopicClick(topic) }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTopicClick(topic) }
                ) {
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = { onTopicClick(topic) }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = AppStrings.trailDetailsViewDetails,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                if (topic.subTopics.isNotEmpty()) {
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = AppStrings.trailDetailsExpandSubtopics,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (topic.subTopics.isNotEmpty()) {
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .padding(start = 28.dp, top = 12.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        topic.subTopics.forEach { subTopic ->
                            SubTopicItem(
                                subTopic = subTopic,
                                onSubTopicClick = { onTopicClick(subTopic) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubTopicItem(
    subTopic: TopicResponse,
    onSubTopicClick: () -> Unit
) {
    val isSubCompleted = subTopic.status.equals("COMPLETED", ignoreCase = true)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSubTopicClick() }
    ) {
        Icon(
            imageVector = if (isSubCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = subTopic.title,
            tint = if (isSubCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = subTopic.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = AppStrings.trailDetailsViewDetails,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
    }
}