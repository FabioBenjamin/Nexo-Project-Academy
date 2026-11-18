package com.nexo.ui.settings

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.nexo.R
import com.nexo.dto.UserSettingsDTO
import com.nexo.security.TokenManager
import com.nexo.ui.navigation.NexoBottomBar
import com.nexo.ui.navigation.Routes
import com.nexo.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onNavigateToEditProfile: (String, String) -> Unit = { _, _ -> },
    onNavigateToLearningQuiz: () -> Unit = {},
    onNavigateBottom: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val coroutineScope = rememberCoroutineScope()
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadSettings()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val currentSuccess = uiState as? SettingsUiState.Success ?: return@rememberLauncherForActivityResult
        if (isGranted) {
            val updated = currentSuccess.settings.copy(notifications = true)
            viewModel.updateSettings(updated)
            coroutineScope.launch {
                tokenManager.saveNotificationPreferences(
                    notificationsEnabled = true,
                    studyWeekendsEnabled = updated.studyWeekends
                )
            }
        } else {
            val updated = currentSuccess.settings.copy(notifications = false)
            viewModel.updateSettings(updated)
            coroutineScope.launch {
                tokenManager.saveNotificationPreferences(
                    notificationsEnabled = false,
                    studyWeekendsEnabled = updated.studyWeekends
                )
            }
            showPermissionDeniedDialog = true
        }
    }

    LaunchedEffect(uiState) {
        val currentSuccess = uiState as? SettingsUiState.Success ?: return@LaunchedEffect
        val hasSystemPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (currentSuccess.settings.notifications && !hasSystemPermission) {
            viewModel.updateSettings(currentSuccess.settings.copy(notifications = false))
            tokenManager.saveNotificationPreferences(
                notificationsEnabled = false,
                studyWeekendsEnabled = currentSuccess.settings.studyWeekends
            )
        } else {
            tokenManager.saveNotificationPreferences(
                notificationsEnabled = currentSuccess.settings.notifications,
                studyWeekendsEnabled = currentSuccess.settings.studyWeekends
            )
        }
    }

    fun handleSettingsUpdate(updated: UserSettingsDTO, requestingNotificationPermission: Boolean) {
        if (!requestingNotificationPermission) {
            viewModel.updateSettings(updated)
            coroutineScope.launch {
                tokenManager.saveNotificationPreferences(
                    notificationsEnabled = updated.notifications,
                    studyWeekendsEnabled = updated.studyWeekends
                )
            }
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                viewModel.updateSettings(updated)
                coroutineScope.launch {
                    tokenManager.saveNotificationPreferences(
                        notificationsEnabled = updated.notifications,
                        studyWeekendsEnabled = updated.studyWeekends
                    )
                }
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            viewModel.updateSettings(updated)
            coroutineScope.launch {
                tokenManager.saveNotificationPreferences(
                    notificationsEnabled = updated.notifications,
                    studyWeekendsEnabled = updated.studyWeekends
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.settingsTitle,
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            NexoBottomBar(
                currentRoute = Routes.SETTINGS,
                onNavigate = onNavigateBottom
            )
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
                    .padding(20.dp)
            ) {
                when (val state = uiState) {
                    is SettingsUiState.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
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
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadSettings() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(AppStrings.btnTryAgain, color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }

                    is SettingsUiState.Success -> {
                        SettingsContent(
                            settings = state.settings,
                            onSettingsChanged = { updated, requestPermission ->
                                handleSettingsUpdate(updated, requestPermission)
                            },
                            onNavigateToEditProfile = {
                                onNavigateToEditProfile("", state.settings.email)
                            },
                            onNavigateToLearningQuiz = onNavigateToLearningQuiz
                        )
                    }
                }
            }
        }
    }

    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.NotificationsOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text(AppStrings.notificationPermissionDialogTitle) },
            text = { Text(AppStrings.notificationPermissionDialogMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDeniedDialog = false
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text(AppStrings.notificationOpenSettingsBtn, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDeniedDialog = false }) {
                    Text(AppStrings.btnCancel)
                }
            }
        )
    }
}

@Composable
fun SettingsContent(
    settings: UserSettingsDTO,
    onSettingsChanged: (UserSettingsDTO, Boolean) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLearningQuiz: () -> Unit
) {
    val hasLearningProfile = !settings.learningProfile.isNullOrBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mascote_calma),
                        contentDescription = AppStrings.cdMascotCalm,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = AppStrings.settingsHeaderTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = AppStrings.settingsHeaderSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Card: Editar Perfil
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToEditProfile() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = AppStrings.editProfileTitle,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = AppStrings.editProfileTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = AppStrings.editProfileHeader,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Card: Perfil de Aprendizagem (Quiz Opcional / Refazer)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToLearningQuiz() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = AppStrings.settingsLearningQuizTitle,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = AppStrings.settingsLearningQuizTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (hasLearningProfile) {
                                AppStrings.settingsLearningQuizConfigured
                            } else {
                                AppStrings.settingsLearningQuizPending
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Switch 1: Notificações diárias
        SettingSwitchItem(
            title = AppStrings.settingsNotificationsTitle,
            subtitle = AppStrings.settingsNotificationsSubtitle,
            checked = settings.notifications,
            onCheckedChange = { isChecked ->
                onSettingsChanged(settings.copy(notifications = isChecked), isChecked)
            }
        )

        // Switch 2: Estudar nos fins de semana
        SettingSwitchItem(
            title = AppStrings.settingsWeekendsTitle,
            subtitle = AppStrings.settingsWeekendsSubtitle,
            checked = settings.studyWeekends,
            onCheckedChange = { isChecked ->
                val needsPermission = isChecked && settings.notifications
                onSettingsChanged(settings.copy(studyWeekends = isChecked), needsPermission)
            }
        )

        // Seletor 3: Tema (Padrão do Sistema / Claro / Escuro)
        SegmentedPreferenceCard(
            title = AppStrings.settingsThemeSectionTitle,
            subtitle = AppStrings.settingsThemeSubtitle,
            options = listOf(
                "system" to AppStrings.settingsThemeSystem,
                "light" to AppStrings.settingsThemeLight,
                "dark" to AppStrings.settingsThemeDark
            ),
            selectedValue = settings.theme,
            onSelect = { newTheme ->
                onSettingsChanged(settings.copy(theme = newTheme), false)
            }
        )

        // Seletor 4: Idioma (Padrão do Sistema / Português / English)
        SegmentedPreferenceCard(
            title = AppStrings.settingsLanguageSectionTitle,
            subtitle = AppStrings.settingsLanguageSubtitle,
            options = listOf(
                "system" to AppStrings.settingsLanguageSystem,
                "pt-BR" to AppStrings.settingsLanguagePt,
                "en-US" to AppStrings.settingsLanguageEn
            ),
            selectedValue = settings.language,
            onSelect = { newLang ->
                onSettingsChanged(settings.copy(language = newLang), false)
            }
        )
    }
}

@Composable
private fun SegmentedPreferenceCard(
    title: String,
    subtitle: String,
    options: List<Pair<String, String>>,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, (value, label) ->
                    val isSelected = selectedValue.equals(value, ignoreCase = true) ||
                            (value == "system" && options.none { it.first.equals(selectedValue, ignoreCase = true) })

                    SegmentedButton(
                        selected = isSelected,
                        onClick = { onSelect(value) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = MaterialTheme.colorScheme.primary,
                            activeContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    }
                }
            }
        }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.surface,
                    checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}