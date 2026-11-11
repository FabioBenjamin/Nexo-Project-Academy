package com.nexo.ui.home

import com.nexo.dto.StudyTrailSummaryResponse
import com.nexo.dto.UserProfileResponse

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val trails: List<StudyTrailSummaryResponse>, val profile: UserProfileResponse? = null) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
