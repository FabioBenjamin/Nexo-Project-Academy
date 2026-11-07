package com.nexo.ui.theme

import androidx.compose.material3.MaterialTheme
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

@Composable
fun NexoAcademyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NexoLightColorScheme,
        typography = Typography, // It will be configured with the Nunito font
        content = content
    )
}