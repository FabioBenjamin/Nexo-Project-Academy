package com.nexo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.SettingsApiService
import com.nexo.dto.LearningProfileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LearningProfileViewModel(
    private val settingsApiService: SettingsApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningQuizUiState())
    val uiState: StateFlow<LearningQuizUiState> = _uiState.asStateFlow()

    fun onSelectSingleOption(questionId: String, option: String) {
        _uiState.update { it.copy(singleAnswers = it.singleAnswers + (questionId to option)) }
    }

    fun onToggleMultipleOption(questionId: String, option: String) {
        _uiState.update { state ->
            val current = state.multipleAnswers[questionId] ?: emptySet()
            val updated = if (option.startsWith("Nenhuma")) {
                setOf(option)
            } else {
                val withoutNone = current.filterNot { it.startsWith("Nenhuma") }.toSet()
                if (withoutNone.contains(option)) withoutNone - option else withoutNone + option
            }
            state.copy(multipleAnswers = state.multipleAnswers + (questionId to updated))
        }
    }

    fun onUpdateTextAnswer(questionId: String, text: String) {
        _uiState.update { it.copy(textAnswers = it.textAnswers + (questionId to text)) }
    }

    fun onNextStep() {
        _uiState.update { state ->
            if (state.currentStep < state.totalSteps - 1) {
                state.copy(currentStep = state.currentStep + 1, errorMessage = null)
            } else state
        }
    }

    fun onPreviousStep() {
        _uiState.update { state ->
            if (state.currentStep > 0) {
                state.copy(currentStep = state.currentStep - 1, errorMessage = null)
            } else state
        }
    }

    fun skipQuiz(onSkipped: () -> Unit) {
        viewModelScope.launch {
            try {
                settingsApiService.skipOnboardingQuiz()
            } catch (_: Exception) {
            } finally {
                onSkipped()
            }
        }
    }

    fun submitProfileQuiz() {
        val consolidatedRules = _uiState.value.buildConsolidatedProfileRules()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = settingsApiService.updateLearningProfile(
                    LearningProfileRequest(learningProfile = consolidatedRules)
                )
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isCompleted = true) }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Erro ao salvar perfil (${response.code()})")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Falha de conexão: ${e.localizedMessage}")
                }
            }
        }
    }

    fun resetQuizState() {
        _uiState.value = LearningQuizUiState()
    }
}