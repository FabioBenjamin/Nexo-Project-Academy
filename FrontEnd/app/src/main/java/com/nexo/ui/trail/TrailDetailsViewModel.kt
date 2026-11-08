/* package com.nexo.ui.trail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.TrailApiService
import com.nexo.dto.StudyTrailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// Representa os possíveis estados da tela de detalhes da trilha
sealed interface TrailDetailsUiState {
    data object Loading : TrailDetailsUiState // Carregando dados da API
    data class Success(val trail: StudyTrailResponse) : TrailDetailsUiState // Trilha carregada com sucesso
    data class Error(val message: String) : TrailDetailsUiState // Falha ao buscar os dados
}

// ViewModel responsável por buscar e gerenciar os dados de uma trilha específica
class TrailDetailsViewModel(
    private val trailApiService: TrailApiService,
    private val trailId: Long
) : ViewModel() {

    // Estado interno mutável da interface
    private val _uiState = MutableStateFlow<TrailDetailsUiState>(TrailDetailsUiState.Loading)
    // Estado público imutável observado pela TrailDetailsScreen
    val uiState: StateFlow<TrailDetailsUiState> = _uiState.asStateFlow()

    init {
        // Dispara o carregamento inicial assim que a ViewModel é instanciada
        loadTrailDetails()
    }

    // Busca os detalhes completos da trilha (incluindo tópicos e subtópicos) no backend
    fun loadTrailDetails() {
        _uiState.value = TrailDetailsUiState.Loading
        viewModelScope.launch {
            try {
                val response = trailApiService.getTrailDetails(trailId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = TrailDetailsUiState.Success(response.body()!!)
                } else {
                    _uiState.value = TrailDetailsUiState.Error("Erro ao carregar detalhes (${response.code()})")
                }
            } catch (e: IOException) {
                // Erro de conexão (sem internet, servidor offline ou timeout)
                _uiState.value = TrailDetailsUiState.Error("Falha de rede. Verifique sua conexão.")
            } catch (e: HttpException) {
                // Erro na resposta HTTP do servidor
                _uiState.value = TrailDetailsUiState.Error("Erro no servidor.")
            } catch (e: Exception) {
                // Qualquer outra exceção inesperada (ex: erro de conversão JSON)
                _uiState.value = TrailDetailsUiState.Error("Falha inesperada ao carregar a trilha.")
            }
        }
    }
} */