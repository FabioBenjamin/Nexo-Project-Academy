package com.nexo.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

object AppPreferencesState {
    var themeMode by mutableStateOf("system")
    var languageMode by mutableStateOf("system")

    val language: String
        get() = when {
            languageMode.equals("system", ignoreCase = true) -> {
                if (Locale.getDefault().language.equals("en", ignoreCase = true)) "en-US" else "pt-BR"
            }
            languageMode.isBlank() -> "pt-BR"
            else -> languageMode
        }

    fun updateFromSettings(theme: String, lang: String) {
        themeMode = theme.ifBlank { "system" }
        languageMode = lang.ifBlank { "system" }
    }
}