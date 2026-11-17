package com.nexo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NexoLightColorScheme = lightColorScheme(
    primary = RoxoAcolhedor,
    onPrimary = BrancoPuro,
    secondary = AmareloAcolhedor,
    onSecondary = TextoPrincipal,
    tertiary = VerdeEquilibrio,
    onTertiary = BrancoPuro,
    background = CremeSuave,
    onBackground = TextoPrincipal,
    surface = BrancoPuro,
    onSurface = TextoPrincipal,
    error = VermelhoErro,
    onError = BrancoPuro
)

private val NexoDarkColorScheme = darkColorScheme(
    primary = RoxoAcolhedor,
    onPrimary = TextoPrincipal,
    secondary = AmareloAcolhedor,
    onSecondary = TextoPrincipal,
    tertiary = VerdeEquilibrio,
    onTertiary = TextoPrincipal,
    background = FundoEscuro,
    onBackground = TextoClaroPrincipal,
    surface = SuperficieEscura,
    onSurface = TextoClaroPrincipal,
    surfaceVariant = BordaEscura,
    onSurfaceVariant = TextoClaroSecundario,
    error = VermelhoErro,
    onError = BrancoPuro
)

fun NexoAcademyTheme(
    themeMode: String = AppPreferencesState.themeMode,
    content: @Composable () -> Unit
) {
    val isDark = when {
        themeMode.equals("dark", ignoreCase = true) -> true
        themeMode.equals("light", ignoreCase = true) -> false
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) NexoDarkColorScheme else NexoLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}