package com.nexo.ui.test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.R
import com.nexo.dto.QuestionExplanationResponse
import com.nexo.dto.QuestionResponse
import com.nexo.ui.theme.AppStrings
import com.nexo.ui.trail.GamificationRewardDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTestScreen(
    viewModel: StudyTestViewModel,
    onBackClick: () -> Unit,
    onUnauthorized: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val rewardEvent by viewModel.rewardEvent.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is StudyTestUiState.Unauthorized) {
            onUnauthorized()
        }
    }

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
                            text = AppStrings.testScreenTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            when (val state = uiState) {
                is StudyTestUiState.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is StudyTestUiState.Unauthorized -> Unit
                is StudyTestUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadTest() },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = AppStrings.btnTryAgain,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
                is StudyTestUiState.InProgress -> {
                    val answeredCount = state.selectedAnswers.size
                    val totalCount = state.questions.size

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            TestHeaderCard(
                                answeredCount = answeredCount,
                                totalCount = totalCount,
                                passingScore = viewModel.requiredScore
                            )
                        }

                        itemsIndexed(state.questions) { index, question ->
                            QuestionCard(
                                index = index + 1,
                                question = question,
                                selectedAlternativeId = state.selectedAnswers[question.id],
                                explanation = null,
                                onSelectAlternative = { altId ->
                                    viewModel.selectAlternative(question.id, altId)
                                }
                            )
                        }

                        if (state.submitError != null) {
                            item {
                                Text(
                                    text = state.submitError,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        item {
                            Button(
                                onClick = { viewModel.submitTest() },
                                enabled = answeredCount == totalCount && !state.isSubmitting,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                            ) {
                                if (state.isSubmitting) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Text(
                                        text = AppStrings.testSubmitBtn,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
                is StudyTestUiState.Result -> {
                    val explanationsByQuestionId = state.result.questionExplanations.associateBy { it.id }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            TestResultBanner(
                                score = state.result.score,
                                total = state.questions.size,
                                passingScore = viewModel.requiredScore,
                                approved = state.result.approved
                            )
                        }

                        itemsIndexed(state.questions) { index, question ->
                            QuestionCard(
                                index = index + 1,
                                question = question,
                                selectedAlternativeId = state.selectedAnswers[question.id],
                                explanation = explanationsByQuestionId[question.id],
                                onSelectAlternative = {}
                            )
                        }

                        item {
                            if (state.result.approved) {
                                Button(
                                    onClick = onBackClick,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                ) {
                                    Text(
                                        text = AppStrings.testBackToTrailBtn,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontSize = 16.sp
                                    )
                                }
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = onBackClick,
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onBackground
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp)
                                    ) {
                                        Text(
                                            text = AppStrings.testReviewTrailBtn,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                    Button(
                                        onClick = { viewModel.loadTest() },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp)
                                    ) {
                                        Text(
                                            text = AppStrings.btnTryAgain,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            rewardEvent?.let { event ->
                GamificationRewardDialog(
                    event = event,
                    onDismiss = { viewModel.dismissRewardEvent() }
                )
            }
        }
    }
}

@Composable
private fun TestHeaderCard(
    answeredCount: Int,
    totalCount: Int,
    passingScore: Int
) {
    val progress = if (totalCount > 0) {
        (answeredCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = AppStrings.testHeaderTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = AppStrings.testHeaderSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.testMinScoreLabel(passingScore),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "$answeredCount / $totalCount",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Image(
                painter = painterResource(id = R.drawable.mascote_focado),
                contentDescription = AppStrings.testHeaderTitle,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(86.dp)
            )
        }
    }
}

@Composable
private fun TestResultBanner(
    score: Int,
    total: Int,
    passingScore: Int,
    approved: Boolean
) {
    val containerColor = if (approved) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val accentColor = if (approved) Color(0xFF2E7D32) else Color(0xFFC62828)
    val mascotRes = if (approved) R.drawable.mascote_default else R.drawable.mascote_apoio

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (approved) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (approved) AppStrings.testResultApprovedTitle else AppStrings.testResultFailedTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = AppStrings.testResultSummary(score = score, total = total, passingScore = passingScore),
                    style = MaterialTheme.typography.bodyMedium,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = mascotRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(84.dp)
            )
        }
    }
}

@Composable
private fun QuestionCard(
    index: Int,
    question: QuestionResponse,
    selectedAlternativeId: Long?,
    explanation: QuestionExplanationResponse?,
    onSelectAlternative: (Long) -> Unit
) {
    val isReviewMode = explanation != null

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = AppStrings.testQuestionBadge(index),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isReviewMode) {
                    val badgeBg = if (explanation!!.isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    val badgeText = if (explanation.isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (explanation.isCorrect) AppStrings.testCorrectBadge else AppStrings.testIncorrectBadge,
                            style = MaterialTheme.typography.labelMedium,
                            color = badgeText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = question.statement,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            question.alternatives.forEach { alt ->
                val isSelected = selectedAlternativeId == alt.id
                val borderColor = when {
                    isReviewMode && isSelected && explanation!!.isCorrect -> Color(0xFF2E7D32)
                    isReviewMode && isSelected && !explanation!!.isCorrect -> Color(0xFFC62828)
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
                    color = if (isSelected) borderColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = !isReviewMode) { onSelectAlternative(alt.id) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = if (isReviewMode) null else { { onSelectAlternative(alt.id) } },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = borderColor,
                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = alt.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (explanation != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (explanation.isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = explanation.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (explanation.isCorrect) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                    )
                }
            }
        }
    }
}