package com.nexo.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexo.dto.UserSettingsDTO
import com.nexo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configurações",
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
                .padding(20.dp)
        ) {
            when (val state = uiState) {
                is SettingsUiState.Loading -> {
                    CircularProgressIndicator(
                        color = RoxoAcolhedor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SettingsUiState.Error -> {
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
                            onClick = { viewModel.loadSettings() },
                            colors = ButtonDefaults.buttonColors(containerColor = RoxoAcolhedor)
                        ) {
                            Text("Tentar Novamente", color = BrancoPuro)
                        }
                    }
                }
                is SettingsUiState.Success -> {
                    SettingsContent(
                        settings = state.settings,
                        onSettingsChanged = { updated -> viewModel.updateSettings(updated) }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsContent(
    settings: UserSettingsDTO,
    onSettingsChanged: (UserSettingsDTO) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Notifications Toggle Row
        SettingSwitchItem(
            title = "Notificações",
            subtitle = "Receber lembretes de estudos diários",
            checked = settings.notifications,
            onCheckedChange = { isChecked -> onSettingsChanged(settings.copy(notifications = isChecked)) }
        )

        // Study on Weekends Toggle Row
        SettingSwitchItem(
            title = "Estudar nos Finais de Semana",
            subtitle = "Incluir sábados e domingos no cronograma",
            checked = settings.studyWeekends,
            onCheckedChange = { isChecked -> onSettingsChanged(settings.copy(studyWeekends = isChecked)) }
        )
    }
}

@Composable
fun SettingSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BrancoPuro,
                    checkedTrackColor = RoxoAcolhedor,
                    uncheckedThumbColor = TextoSecundario,
                    uncheckedTrackColor = CinzaDesabilitado
                )
            )
        }
    }
}