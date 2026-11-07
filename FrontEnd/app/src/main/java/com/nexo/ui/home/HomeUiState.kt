package com.nexo.ui.home

import com.nexo.dto.StudyTrailSummaryResponse
sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val trails: List<StudyTrailSummaryResponse>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
