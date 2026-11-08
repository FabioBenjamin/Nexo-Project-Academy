package com.nexo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.nexo.ui.theme.*

// Navigation item representation for the bottom bar
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem(Routes.HOME, "Início", Icons.Default.Home)
    object Profile : BottomNavItem(Routes.PROFILE, "Perfil", Icons.Default.Person)
    object Settings : BottomNavItem(Routes.SETTINGS, "Ajustes", Icons.Default.Settings)
}

@Composable
fun NexoBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile,
        BottomNavItem.Settings
    )

    NavigationBar(
        containerColor = BrancoPuro,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) RoxoAcolhedor else TextoSecundario
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) RoxoAcolhedor else TextoSecundario
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = CremeSuave
                )
            )
        }
    }
}