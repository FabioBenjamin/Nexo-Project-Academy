package com.example.nexoacademy.ui.trail

sealed interface CreateTrailUiState {
    object Idle : CreateTrailUiState
    object Loading : CreateTrailUiState
    data class Success(val trailId: Long) : CreateTrailUiState
    data class Error(val message: String) : CreateTrailUiState
}