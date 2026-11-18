package com.nexo.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.dto.UserProfileResponse
import com.nexo.security.TokenManager
import com.nexo.ui.navigation.NexoBottomBar
import com.nexo.ui.navigation.Routes
import com.nexo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onNavigateToEditProfile: (String, String) -> Unit = { _, _ -> },
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateBottom: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.profileTitle,
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
                    if (uiState is ProfileUiState.Success) {
                        val profile = (uiState as ProfileUiState.Success).profile
                        IconButton(onClick = { onNavigateToEditProfile(profile.username, profile.email) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = AppStrings.editProfileTitle,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = AppStrings.settingsTitle,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            NexoBottomBar(
                currentRoute = Routes.PROFILE,
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
                    .padding(horizontal = 20.dp)
            ) {
                when (val state = uiState) {
                    is ProfileUiState.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is ProfileUiState.Error -> {
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
                                onClick = { viewModel.loadProfile() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(AppStrings.btnTryAgain, color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                    is ProfileUiState.Success -> {
                        ProfileContent(
                            profile = state.profile,
                            onNavigateToEditProfile = {
                                onNavigateToEditProfile(state.profile.username, state.profile.email)
                            },
                            onNavigateToAchievements = onNavigateToAchievements
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    profile: UserProfileResponse,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {}
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val avatarId by tokenManager.avatarIdFlow.collectAsState(initial = tokenManager.cachedAvatarId)
    val avatarResId = MascotAvatarHelper.getResIdById(avatarId)

    val unlockedCount = profile.achievements.count { it.unlocked }
    val totalAchievementsCount = profile.achievements.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToEditProfile() }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(126.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.78f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(98.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(3.dp, MaterialTheme.colorScheme.secondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = avatarResId),
                            contentDescription = AppStrings.cdMascotDefault,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = profile.username,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = profile.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    StreakBadge(
                        streakDays = profile.currentStreak,
                        studiedToday = profile.studiedToday
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "XP",
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${AppStrings.profileLevelLabel}${profile.level}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "${profile.currentXp} / ${profile.maxXp} XP",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val progressValue = if (profile.maxXp > 0) profile.currentXp.toFloat() / profile.maxXp else 0f
                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFFFFB300),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = AppStrings.profileOverviewHeader,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OverviewStatCard(
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = if (profile.studiedToday) Color(0xFFFF6D00) else Color(0xFF9CA3AF),
                    value = AppStrings.profileDaysUnit(profile.currentStreak),
                    label = AppStrings.profileStatStreakTitle,
                    modifier = Modifier.weight(1f)
                )
                OverviewStatCard(
                    icon = Icons.Default.Bolt,
                    iconTint = Color(0xFFFFB300),
                    value = "${profile.totalXp} XP",
                    label = AppStrings.profileStatTotalXpTitle,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OverviewStatCard(
                    icon = Icons.Default.EmojiEvents,
                    iconTint = Color(0xFF8B5CF6),
                    value = AppStrings.profileDaysUnit(profile.maxStreak),
                    label = AppStrings.profileStatBestStreakTitle,
                    modifier = Modifier.weight(1f)
                )
                OverviewStatCard(
                    icon = Icons.Default.Flag,
                    iconTint = MaterialTheme.colorScheme.tertiary,
                    value = "${profile.completedTrailsCount}",
                    label = AppStrings.profileStatTrailsTitle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Color(0xFFFFB300).copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToAchievements() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFB300).copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.profileAchievementsCardTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = AppStrings.profileAchievementsProgressSummary(
                                unlocked = unlockedCount,
                                total = totalAchievementsCount
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                val achievementProgress = if (totalAchievementsCount > 0) {
                    unlockedCount.toFloat() / totalAchievementsCount.toFloat()
                } else {
                    0f
                }

                LinearProgressIndicator(
                    progress = { achievementProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFFFB300),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onNavigateToAchievements,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = AppStrings.profileViewAllAchievementsBtn,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun OverviewStatCard(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}