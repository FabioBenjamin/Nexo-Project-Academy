package com.nexo.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nexo.R
import com.nexo.dto.UserSettingsDTO
import com.nexo.ui.navigation.NexoBottomBar
import com.nexo.ui.navigation.Routes
import com.nexo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Configurações", style = MaterialTheme.typography.titleLarge, color = TextoPrincipal)
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
        bottomBar = {
            NexoBottomBar(
                currentRoute = Routes.SETTINGS,
                onNavigate = onNavigateBottom
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
        // Real Mascot Calm Pose Image Header
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BrancoPuro),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mascote_calma),
                    contentDescription = "Mascote Calma",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Ajustes de Aprendizado",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoPrincipal
                    )
                    Text(
                        text = "Personalize o ritmo e lembretes da sua rotina.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                }
            }
        }

        SettingSwitchItem(
            title = "Notificações Diárias",
            subtitle = "Receber lembretes amigáveis para estudar",
            checked = settings.notifications,
            onCheckedChange = { isChecked: Boolean ->
                onSettingsChanged(settings.copy(notifications = isChecked))
            }
        )

        SettingSwitchItem(
            title = "Estudar nos Finais de Semana",
            subtitle = "Incluir sábados e domingos nas suas metas",
            checked = settings.studyWeekends,
            onCheckedChange = { isChecked: Boolean ->
                onSettingsChanged(settings.copy(studyWeekends = isChecked))
            }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
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
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BrancoPuro,
                    checkedTrackColor = VerdeEquilibrio,
                    uncheckedThumbColor = CinzaDesabilitado,
                    uncheckedTrackColor = CremeSuave
                )
            )
        }
    }
}