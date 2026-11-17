package com.nexo.data.api

import com.nexo.dto.UserSettingsDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface SettingsApiService {
        /**
         * Retrieves the current user's preferences
         * Endpoint: GET /settings
         */
        @GET("settings")
        suspend fun getSettings(): Response<UserSettingsDTO>

        /**
         * Updates the current user's preferences
         * Endpoint: PUT /settings
         */
        @PUT("settings")
        suspend fun updateSettings(@Body settings: UserSettingsDTO): Response<UserSettingsDTO>

        @PATCH("settings/learning-profile")
        suspend fun updateLearningProfile(@Body request: LearningProfileRequest): Response<Unit>

        @PATCH("settings/onboarding/skip")
        suspend fun skipOnboardingQuiz(): Response<Unit>
    }