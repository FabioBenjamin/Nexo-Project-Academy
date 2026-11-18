package com.nexo.data.api

import com.nexo.dto.AccessTokenResponse
import com.nexo.dto.ForgotPasswordRequest
import com.nexo.dto.LoginResponseDTO
import com.nexo.dto.LoginUserDTO
import com.nexo.dto.RefreshTokenRequest
import com.nexo.dto.RegisterUserDTO
import com.nexo.dto.ResetPasswordRequest

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(
        @Body dto: RegisterUserDTO
    ): Response<Unit>

    @POST("auth/login")
    suspend fun login(
        @Body dto: LoginUserDTO
    ): Response<LoginResponseDTO>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<AccessTokenResponse>

    @POST("auth/logout")
    suspend fun logout(
        @Body request: RefreshTokenRequest
    ): Response<Unit>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>
}
