package com.nexo.ui.trail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexo.data.api.TrailApiService
import com.nexo.dto.StudyTrailRequestDTO
import com.nexo.ui.theme.AppStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate

class CreateTrailViewModel(
    private val trailApiService: TrailApiService
) : ViewModel() {

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private val _targetDate = MutableStateFlow(getDefaultTargetDate())
    @RequiresApi(Build.VERSION_CODES.O)
    val targetDate: StateFlow<String> = _targetDate.asStateFlow()

    private val _uiState = MutableStateFlow<CreateTrailUiState>(CreateTrailUiState.Idle)
    val uiState: StateFlow<CreateTrailUiState> = _uiState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDefaultTargetDate(): String {
        return LocalDate.now().plusDays(30).toString()
    }

    fun updateContent(newContent: String) {
        _content.value = newContent
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTargetDate(newDate: String) {
        _targetDate.value = newDate
    }

    val isFormValid: Boolean
        @RequiresApi(Build.VERSION_CODES.O)
        get() = content.value.trim().length >= 3 && targetDate.value.isNotBlank()

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateTrail() {
        val trimmedContent = content.value.trim()
        val trimmedDate = targetDate.value.trim()
        if (trimmedContent.isBlank()) {
            _uiState.value = CreateTrailUiState.Error(AppStrings.errMissingSubject)
            return
        }
        if (trimmedContent.length < 3) {
            _uiState.value = CreateTrailUiState.Error(AppStrings.errSubjectLength)
            return
        }

        if (trimmedDate.isBlank()) {
            _uiState.value = CreateTrailUiState.Error(AppStrings.errMissingDate)
            return
        }

        if (_uiState.value is CreateTrailUiState.Loading) return

        _uiState.value = CreateTrailUiState.Loading

        viewModelScope.launch {
            try {
                val request = StudyTrailRequestDTO(
                    content = trimmedContent,
                    targetDate = trimmedDate
                )
                val response = trailApiService.generateTrail(request)
                if (response.isSuccessful && response.body() != null) {
                    val createdTrailId = response.body()!!
                    _uiState.value = CreateTrailUiState.Success(createdTrailId)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.takeIf { it.isNotBlank() }
                        ?: "${AppStrings.errServer} (${response.code()})"

                    _uiState.value = CreateTrailUiState.Error(errorMessage)
                }
            } catch (e: IOException) {
                _uiState.value = CreateTrailUiState.Error(AppStrings.errNetwork)
            } catch (e: HttpException) {
                _uiState.value = CreateTrailUiState.Error(AppStrings.errServer)
            } catch (e: Exception) {
                _uiState.value = CreateTrailUiState.Error(e.message ?: AppStrings.errUnexpected)
            }
        }
    }

    fun resetUiState() {
        _uiState.value = CreateTrailUiState.Idle
    }
}