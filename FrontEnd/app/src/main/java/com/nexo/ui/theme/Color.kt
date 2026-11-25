package com.nexo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Nexo Brandbook main palette
val AmareloAcolhedor = Color(0xFFFFC94D)
val RoxoAcolhedor = Color(0xFFB69BFF)
val CremeSuave = Color(0xFFFFF6E1)
val VerdeEquilibrio = Color(0xFF5DBEA6)

// Fixed Text Colors (for internal use by color schemes)
val TextoEscuroFixo = Color(0xFF1A1F2E)
val TextoSecundarioEscuroFixo = Color(0xFF6B7280)

// Secondary and Supporting Colors
val BlushApoio = Color(0xFFFFB6A3)
val AzulNexo = Color(0xFF0B2A6B)
val BrancoPuro = Color(0xFFFFFFFF)
val CinzaDesabilitado = Color(0xFFE0E0E0)
val VermelhoErro = Color(0xFFEF4444)

// Cores do Modo Escuro
val FundoEscuro = Color(0xFF12151E)
val SuperficieEscura = Color(0xFF1E2230)
val TextoClaroPrincipal = Color(0xFFF3F4F6)
val TextoClaroSecundario = Color(0xFF9CA3AF)
val BordaEscura = Color(0xFF2E3446)

// Reactive helper that checks if dark theme is active
val isNexoDarkTheme: Boolean
    @Composable
    get() = when {
        AppPreferencesState.themeMode.equals("dark", ignoreCase = true) -> true
        AppPreferencesState.themeMode.equals("light", ignoreCase = true) -> false
        else -> isSystemInDarkTheme()
    }

// Automatically switches between FixedDarkText (#1A1F2E) and White/Light in Dark Mode
val TextoPrincipal: Color
    @Composable
    get() = if (isNexoDarkTheme) TextoClaroPrincipal else TextoEscuroFixo

val TextoSecundario: Color
    @Composable
    get() = if (isNexoDarkTheme) TextoClaroSecundario else TextoSecundarioEscuroFixo