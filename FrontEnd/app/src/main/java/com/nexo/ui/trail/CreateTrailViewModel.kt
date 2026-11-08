package com.nexo.ui.trail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.TrailApiService
import com.nexo.dto.StudyTrailRequestDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateTrailViewModel(
    private val trailApiService: TrailApiService
) : ViewModel() {

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _targetDate = MutableStateFlow(getDefaultTargetDate())
    val targetDate: StateFlow<String> = _targetDate.asStateFlow()

    private val _uiState = MutableStateFlow<CreateTrailUiState>(CreateTrailUiState.Idle)
    val uiState: StateFlow<CreateTrailUiState> = _uiState.asStateFlow()

    private fun getDefaultTargetDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 30)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    fun updateContent(newContent: String) {
        _content.value = newContent
    }

    fun updateTargetDate(newDate: String) {
        _targetDate.value = newDate
    }

    fun generateTrail() {
        if (_content.value.isBlank()) {
            _uiState.value = CreateTrailUiState.Error("Descreva o assunto que você deseja estudar.")
            return
        }

        if (_targetDate.value.isBlank()) {
            _uiState.value = CreateTrailUiState.Error("Informe a data de término desejada.")
            return
        }

        _uiState.value = CreateTrailUiState.Loading

        viewModelScope.launch {
            try {
                val request = StudyTrailRequestDTO(
                    content = _content.value,
                    targetDate = _targetDate.value
                )
                val response = trailApiService.generateTrail(request)
                if (response.isSuccessful && response.body() != null) {
                    val createdTrail = response.body()!!
                    // CORREÇÃO: Pegando o ID do DTO de resposta da trilha gerada
                    _uiState.value = CreateTrailUiState.Success(createdTrail.id)
                } else {
                    _uiState.value = CreateTrailUiState.Error("Erro ao gerar a trilha (${response.code()})")
                }
            } catch (e: IOException) {
                _uiState.value = CreateTrailUiState.Error("Falha de rede. Verifique sua conexão.")
            } catch (e: HttpException) {
                _uiState.value = CreateTrailUiState.Error("Erro no servidor.")
            } catch (e: Exception) {
                _uiState.value = CreateTrailUiState.Error("Falha inesperada ao gerar a trilha.")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = CreateTrailUiState.Idle
    }
}