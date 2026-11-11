package com.nexo.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppPreferencesState {
    var isDarkTheme by mutableStateOf(false)
    var language by mutableStateOf("pt-BR")

    fun updateFromSettings(theme: String, lang: String) {
        isDarkTheme = theme.equals("dark", ignoreCase = true)
        language = lang.ifBlank { "pt-BR" }
    }
}