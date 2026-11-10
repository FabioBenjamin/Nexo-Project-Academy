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

@Composable
fun NexoAcademyTheme(
    darkTheme: Boolean = AppPreferencesState.isDarkTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NexoDarkColorScheme else NexoLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // It will be configured with the Nunito font
        content = content
    )
}