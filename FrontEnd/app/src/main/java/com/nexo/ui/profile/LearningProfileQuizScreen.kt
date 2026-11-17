package com.nexo.ui.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LearningProfileQuizScreen(
    uiState: LearningQuizUiState,
    onSelectSingleOption: (questionId: String, option: String) -> Unit,
    onToggleMultipleOption: (questionId: String, option: String) -> Unit,
    onUpdateTextAnswer: (questionId: String, text: String) -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onSubmitQuiz: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = uiState.progress,
        label = "QuizProgressAnimation"
    )

    val primaryIndigo = Color(0xFF3F51B5)
    val vibrantPurple = Color(0xFF7C4DFF)
    val surfaceBackground = MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(surfaceBackground)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            if (uiState.currentStep > 0) onPreviousStep() else onNavigateBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }

                    Text(
                        text = "Etapa ${uiState.currentStep + 1} de ${uiState.totalSteps}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = vibrantPurple.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = vibrantPurple,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Perfil IA",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = vibrantPurple
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = vibrantPurple,
                    trackColor = vibrantPurple.copy(alpha = 0.15f)
                )
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    val isLastStep = uiState.currentStep == uiState.totalSteps - 1
                    val canProceed = uiState.isCurrentStepValid()

                    Button(
                        onClick = {
                            if (isLastStep) onSubmitQuiz() else onNextStep()
                        },
                        enabled = canProceed && !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLastStep) vibrantPurple else primaryIndigo
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Salvando perfil...", fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = if (isLastStep) "Salvar Perfil de Aprendizagem" else "Continuar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (isLastStep) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            AnimatedContent(
                targetState = uiState.currentQuestion,
                transitionSpec = {
                    (slideInHorizontally { width -> width / 3 } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> -width / 3 } + fadeOut())
                },
                label = "QuestionTransition"
            ) { question ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = question.stepTitle.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = vibrantPurple,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    question.subtitle?.let { subtitle ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    when (question.type) {
                        QuestionType.SINGLE_CHOICE -> {
                            val selectedOption = uiState.singleAnswers[question.id]
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                question.options.forEach { option ->
                                    QuizOptionCard(
                                        text = option,
                                        isSelected = selectedOption == option,
                                        isMultiSelect = false,
                                        accentColor = primaryIndigo,
                                        onClick = { onSelectSingleOption(question.id, option) }
                                    )
                                }
                            }
                        }

                        QuestionType.MULTIPLE_CHOICE -> {
                            val selectedOptions = uiState.multipleAnswers[question.id] ?: emptySet()
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                question.options.forEach { option ->
                                    QuizOptionCard(
                                        text = option,
                                        isSelected = selectedOptions.contains(option),
                                        isMultiSelect = true,
                                        accentColor = vibrantPurple,
                                        onClick = { onToggleMultipleOption(question.id, option) }
                                    )
                                }
                            }
                        }

                        QuestionType.OPEN_TEXT -> {
                            val currentText = uiState.textAnswers[question.id] ?: ""
                            OutlinedTextField(
                                value = currentText,
                                onValueChange = { onUpdateTextAnswer(question.id, it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 160.dp),
                                placeholder = {
                                    Text(
                                        text = question.placeholder,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = vibrantPurple,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }

                    if (uiState.currentStep == uiState.totalSteps - 1) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Essas preferências ficarão salvas no seu perfil e serão aplicadas automaticamente em todas as trilhas que você criar. Você pode alterá-las a qualquer momento nas Configurações.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(14.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    uiState.errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizOptionCard(
    text: String,
    isSelected: Boolean,
    isMultiSelect: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    val containerColor = if (isSelected) accentColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(if (isMultiSelect) RoundedCornerShape(6.dp) else CircleShape)
                    .background(if (isSelected) accentColor else Color.Transparent)
                    .then(
                        if (!isSelected) {
                            Modifier.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}