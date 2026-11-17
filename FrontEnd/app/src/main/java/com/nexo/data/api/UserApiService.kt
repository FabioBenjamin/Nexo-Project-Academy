package com.nexo.data.api

import com.nexo.dto.ChangePasswordRequest
import com.nexo.dto.ChangeUserEmailRequest
import com.nexo.dto.ChangeUsernameRequest
import com.nexo.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApiService {
    /**
     * Fetches the profile data for the currently authenticated user
     * Endpoint: GET /user/profile
     */
    @GET("user/profile")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    /**
     * Updates the username of the current user
     * Endpoint: PATCH /user/me/username
     */
    @PATCH("/me/username")
    suspend fun changeUsername(@Body request: ChangeUsernameRequest): Response<Unit>

    /**
     * Updates the email of the current user
     * Endpoint: PATCH /user/me/email
     */
    @PATCH("/me/email")
    suspend fun changeEmail(@Body request: ChangeUserEmailRequest): Response<Unit>

    /**
     * Updates the password of the current user
     * Endpoint: PATCH /user/me/password
     */
    @PATCH("/me/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Unit>

    @retrofit2.http.DELETE("/me")
    suspend fun deleteAccount(): Response<Unit>
}