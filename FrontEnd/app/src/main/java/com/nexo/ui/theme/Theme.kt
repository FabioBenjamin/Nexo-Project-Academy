package com.nexo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NexoLightColorScheme = lightColorScheme(
    primary = RoxoAcolhedor,
    onPrimary = BrancoPuro,
    secondary = AmareloAcolhedor,
    onSecondary = TextoEscuroFixo,
    tertiary = VerdeEquilibrio,
    onTertiary = BrancoPuro,
    background = CremeSuave,
    onBackground = TextoEscuroFixo,
    surface = BrancoPuro,
    onSurface = TextoEscuroFixo,
    error = VermelhoErro,
    onError = BrancoPuro
)

private val NexoDarkColorScheme = darkColorScheme(
    primary = RoxoAcolhedor,
    onPrimary = TextoEscuroFixo,
    secondary = AmareloAcolhedor,
    onSecondary = TextoEscuroFixo,
    tertiary = VerdeEquilibrio,
    onTertiary = TextoEscuroFixo,
    background = FundoEscuro,
    onBackground = TextoClaroPrincipal,
    surface = SuperficieEscura,
    onSurface = TextoClaroPrincipal,
    surfaceVariant = BordaEscura,
    onSurfaceVariant = TextoClaroSecundario,
    outline = BordaEscura,
    error = VermelhoErro,
    onError = BrancoPuro
)

@Composable
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
@Composable
fun nexoTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    disabledContainerColor = MaterialTheme.colorScheme.surface,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
)
