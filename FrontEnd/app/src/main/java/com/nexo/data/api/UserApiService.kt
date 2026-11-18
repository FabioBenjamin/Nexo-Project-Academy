package com.nexo.data.api

import com.nexo.dto.ChangePasswordRequest
import com.nexo.dto.ChangeUserEmailRequest
import com.nexo.dto.ChangeUsernameRequest
import com.nexo.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApiService {

    @GET("users/profile")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @PATCH("users/me/username")
    suspend fun changeUsername(@Body request: ChangeUsernameRequest): Response<Unit>

    @PATCH("users/me/email")
    suspend fun changeEmail(@Body request: ChangeUserEmailRequest): Response<Unit>

    @PATCH("users/me/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Unit>

    @DELETE("users/me")
    suspend fun deleteAccount(): Response<Unit>
}