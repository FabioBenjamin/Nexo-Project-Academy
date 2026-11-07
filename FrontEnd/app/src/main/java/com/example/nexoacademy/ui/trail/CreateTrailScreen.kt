package com.example.nexoacademy.ui.trail

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nexoacademy.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTrailScreen(
    viewModel: CreateTrailViewModel,
    onBackClick: () -> Unit,
    onTrailCreatedSuccessfully: () -> Unit
) {
    val content by viewModel.content.collectAsState()
    val targetDate by viewModel.targetDate.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val calendar = Calendar.getInstance()

    // Processa a data armazenada no ViewModel para abrir o calendário na data correta
    val dateParts = targetDate.split("-")
    val year = dateParts.getOrNull(0)?.toIntOrNull() ?: calendar.get(Calendar.YEAR)
    val month = (dateParts.getOrNull(1)?.toIntOrNull()?.minus(1)) ?: calendar.get(Calendar.MONTH)
    val day = dateParts.getOrNull(2)?.toIntOrNull() ?: calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val formattedMonth = (selectedMonth + 1).toString().padStart(2, '0')
            val formattedDay = selectedDayOfMonth.toString().padStart(2, '0')
            viewModel.updateTargetDate("$selectedYear-$formattedMonth-$formattedDay")
        },
        year, month, day
    )

    LaunchedEffect(uiState) {
        if (uiState is CreateTrailUiState.Success) {
            onTrailCreatedSuccessfully()
            viewModel.resetUiState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Trilha com IA", style = MaterialTheme.typography.titleLarge, color = TextoPrincipal) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = TextoPrincipal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CremeSuave)
            )
        },
        containerColor = CremeSuave
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("O que você gostaria de aprender?", style = MaterialTheme.typography.headlineMedium, color = TextoPrincipal, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text("Descreva o assunto e o objetivo. O Gemini irá gerar um plano de estudos completo com etapas e prazos.", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { viewModel.updateContent(it) },
                    label = { Text("Ex: Quero aprender Spring Boot com Kotlin...") },
                    minLines = 4, maxLines = 6, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = BrancoPuro, unfocusedContainerColor = BrancoPuro, focusedBorderColor = RoxoAcolhedor, unfocusedBorderColor = CinzaDesabilitado)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = targetDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data limite de conclusão (YYYY-MM-DD)") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Selecionar Data", tint = RoxoAcolhedor)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = BrancoPuro, unfocusedContainerColor = BrancoPuro, focusedBorderColor = RoxoAcolhedor, unfocusedBorderColor = CinzaDesabilitado)
                )

                if (uiState is CreateTrailUiState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = (uiState as CreateTrailUiState.Error).message, style = MaterialTheme.typography.bodyMedium, color = VermelhoErro, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.generateTrail() },
                    enabled = uiState !is CreateTrailUiState.Loading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoxoAcolhedor, contentColor = BrancoPuro)
                ) {
                    if (uiState is CreateTrailUiState.Loading) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(color = BrancoPuro, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("O Gemini está criando sua trilha...", style = MaterialTheme.typography.labelLarge)
                        }
                    } else {
                        Text("Gerar Trilha com IA", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}