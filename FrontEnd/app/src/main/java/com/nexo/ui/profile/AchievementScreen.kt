package com.nexo.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.dto.UserAchievementDTO
import com.nexo.dto.UserProfileResponse
import com.nexo.ui.theme.AppStrings
import com.nexo.ui.theme.NexoPullToRefreshBox

private enum class AchievementFilter {
    ALL, UNLOCKED, IN_PROGRESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    var selectedFilter by remember { mutableStateOf(AchievementFilter.ALL) }

    LaunchedEffect(Unit) {
        if (uiState !is ProfileUiState.Success) {
            viewModel.loadProfile()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.achievementsScreenTitle,
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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NexoPullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                is ProfileUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadProfile() }) {
                                Text(AppStrings.btnTryAgain)
                            }
                        }
                    }
                }
                is ProfileUiState.Success -> {
                    AchievementsContent(
                        profile = state.profile,
                        selectedFilter = selectedFilter,
                        onSelectFilter = { selectedFilter = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementsContent(
    profile: UserProfileResponse,
    selectedFilter: AchievementFilter,
    onSelectFilter: (AchievementFilter) -> Unit
) {
    val filteredAchievements = remember(profile.achievements, selectedFilter) {
        val sorted = profile.achievements.sortedByDescending { it.unlocked }
        when (selectedFilter) {
            AchievementFilter.ALL -> sorted
            AchievementFilter.UNLOCKED -> sorted.filter { it.unlocked }
            AchievementFilter.IN_PROGRESS -> sorted.filter { !it.unlocked }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = AppStrings.achievementsPersonalRecordsHeader,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        PersonalRecordCard(
                            icon = Icons.Default.LocalFireDepartment,
                            accentColor = Color(0xFFFF6D00),
                            bigValue = "${profile.maxStreak}",
                            title = AppStrings.recordLongestStreak,
                            subtitle = AppStrings.profileDaysUnit(profile.maxStreak)
                        )
                    }
                    item {
                        PersonalRecordCard(
                            icon = Icons.Default.Bolt,
                            accentColor = Color(0xFFFFB300),
                            bigValue = "${profile.totalXp}",
                            title = AppStrings.recordTotalXp,
                            subtitle = "${AppStrings.profileLevelLabel}${profile.level}"
                        )
                    }
                    item {
                        PersonalRecordCard(
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            accentColor = MaterialTheme.colorScheme.primary,
                            bigValue = "${profile.completedActivitiesCount}",
                            title = AppStrings.recordTopicsCompleted,
                            subtitle = "Nexo Academy"
                        )
                    }
                    item {
                        PersonalRecordCard(
                            icon = Icons.Default.Flag,
                            accentColor = MaterialTheme.colorScheme.tertiary,
                            bigValue = "${profile.completedTrailsCount}",
                            title = AppStrings.recordTrailsFinished,
                            subtitle = "${profile.completedTrailsCount}/${profile.totalTrailsCount}"
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = AppStrings.achievementsTriumphsHeader,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedFilter == AchievementFilter.ALL,
                        onClick = { onSelectFilter(AchievementFilter.ALL) },
                        label = { Text(AppStrings.filterAll) }
                    )
                    FilterChip(
                        selected = selectedFilter == AchievementFilter.UNLOCKED,
                        onClick = { onSelectFilter(AchievementFilter.UNLOCKED) },
                        label = { Text(AppStrings.filterUnlocked) }
                    )
                    FilterChip(
                        selected = selectedFilter == AchievementFilter.IN_PROGRESS,
                        onClick = { onSelectFilter(AchievementFilter.IN_PROGRESS) },
                        label = { Text(AppStrings.filterInProgress) }
                    )
                }
            }
        }

        items(filteredAchievements, key = { it.id }) { achievement ->
            TriumphProgressCard(achievement = achievement)
        }
    }
}

@Composable
private fun PersonalRecordCard(
    icon: ImageVector,
    accentColor: Color,
    bigValue: String,
    title: String,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.45f)),
        modifier = Modifier.width(142.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = bigValue,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TriumphProgressCard(achievement: UserAchievementDTO) {
    val unlocked = achievement.unlocked
    val goldAccent = Color(0xFFFFB300)
    val safeTarget = achievement.targetProgress.coerceAtLeast(1)
    val clampedCurrent = if (unlocked) safeTarget else achievement.currentProgress.coerceIn(0, safeTarget)
    val progressFraction = clampedCurrent.toFloat() / safeTarget.toFloat()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = if (unlocked) 2.dp else 1.dp,
            color = if (unlocked) goldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (unlocked) 3.dp else 1.dp),
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
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(
                        if (unlocked) goldAccent else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (unlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                    contentDescription = achievement.title,
                    tint = if (unlocked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = achievement.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (unlocked) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            if (unlocked) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = "+${achievement.xpReward} XP",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (unlocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                if (!achievement.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = achievement.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (unlocked) goldAccent else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = AppStrings.achievementProgressRatio(clampedCurrent, safeTarget),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (unlocked) goldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}