package com.example.nexoacademy.ui.trail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexoacademy.data.api.TrailApiService
import com.example.nexoacademy.data.dto.StudyTrailRequestDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateTrailViewModel(
    private val trailApiService: TrailApiService
) : ViewModel() {

    var content = MutableStateFlow("")
    var targetDate = MutableStateFlow(getDefaultTargetDate())

    private val _uiState = MutableStateFlow<CreateTrailUiState>(CreateTrailUiState.Idle)
    val uiState: StateFlow<CreateTrailUiState> = _uiState.asStateFlow()

    private fun getDefaultTargetDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 30) // Padrão: 30 dias a partir de hoje
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    fun generateTrail() {
        if (content.value.isBlank()) {
            _uiState.value = CreateTrailUiState.Error("Descreva o assunto que você deseja estudar.")
            return
        }

        if (targetDate.value.isBlank()) {
            _uiState.value = CreateTrailUiState.Error("Informe a data de término desejada.")
            return
        }

        _uiState.value = CreateTrailUiState.Loading

        viewModelScope.launch {
            try {
                // Instancia usando o campo 'content' correto do DTO
                val request = StudyTrailRequestDTO(
                    content = content.value,
                    targetDate = targetDate.value
                )

                val response = trailApiService.generateTrail(request)

                if (response.isSuccessful && response.body() != null) {
                    val createdTrail = response.body()!!
                    _uiState.value = CreateTrailUiState.Success(createdTrail.id)
                } else {
                    _uiState.value = CreateTrailUiState.Error("Erro ao gerar a trilha (${response.code()})")
                }
            } catch (e: Exception) {
                _uiState.value = CreateTrailUiState.Error(e.message ?: "Falha na conexão ao gerar a trilha")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = CreateTrailUiState.Idle
    }
}