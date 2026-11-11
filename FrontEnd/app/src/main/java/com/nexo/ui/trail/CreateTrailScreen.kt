package com.nexo.ui.trail

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
import com.nexo.ui.theme.*
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
                title = { Text(AppStrings.createTrailTitle, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = AppStrings.btnBack, tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(AppStrings.createTrailHeader, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text(AppStrings.createTrailDescription, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { viewModel.updateContent(it) },
                    label = { Text(AppStrings.createTrailInputPlaceholder) },
                    minLines = 4, maxLines = 6, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { viewModel.updateTargetDate(it) },
                    readOnly = true,
                    label = { Text(AppStrings.createTrailDateLabel) },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = AppStrings.createTrailSelectDate, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
                )

                if (uiState is CreateTrailUiState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = (uiState as CreateTrailUiState.Error).message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.generateTrail() },
                    enabled = uiState !is CreateTrailUiState.Loading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    if (uiState is CreateTrailUiState.Loading) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(AppStrings.createTrailLoadingText, style = MaterialTheme.typography.labelLarge)
                        }
                    } else {
                        Text(AppStrings.createTrailBtnSubmit, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
