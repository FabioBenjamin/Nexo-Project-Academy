package com.example.nexoacademy.ui.home

import com.example.nexoacademy.data.dto.StudyTrailSummaryResponse
sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val trails: List<StudyTrailSummaryResponse>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
