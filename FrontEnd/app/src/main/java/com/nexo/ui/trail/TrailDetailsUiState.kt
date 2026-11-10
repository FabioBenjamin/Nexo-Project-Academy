package com.nexo.ui.trail

import com.nexo.dto.StudyTrailResponse

sealed interface TrailDetailsUiState {
    object Loading : TrailDetailsUiState
    data class Success(val trail: StudyTrailResponse) : TrailDetailsUiState
    data class Error(val message: String) : TrailDetailsUiState
}
