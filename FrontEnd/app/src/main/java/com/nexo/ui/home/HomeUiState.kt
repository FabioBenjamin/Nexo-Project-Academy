package com.nexo.ui.home

import com.nexo.dto.StudyTrailSummaryResponse
import com.nexo.dto.UserProfileResponse

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val trails: List<StudyTrailSummaryResponse>,
        val profile: UserProfileResponse? = null,
        val isLoadingMore: Boolean = false,
        val isLastPage: Boolean = false
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
    data object Unauthorized : HomeUiState
}