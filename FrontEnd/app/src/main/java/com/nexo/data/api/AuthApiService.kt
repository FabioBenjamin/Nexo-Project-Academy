package com.nexo.data.api

import com.nexo.dto.AccessTokenResponse
import com.nexo.dto.LoginResponseDTO
import com.nexo.dto.LoginUserDTO
import com.nexo.dto.RefreshTokenRequest
import com.nexo.dto.RegisterUserDTO

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    // TODO: New user registration
    // TODO: Route - POST /auth/register
    // TODO: Expected response - 201 Created
    @POST("auth/register")
    suspend fun register(
        @Body dto: RegisterUserDTO
    ): Response<Unit>

    // TODO: User login
    // TODO: Route - POST /auth/login
    // TODO: Expected response - 200 OK with tokens
    @POST("auth/login")
    suspend fun login(
        @Body dto: LoginUserDTO
    ): Response<LoginResponseDTO>

    // TODO: Acess Token Refresh
    // TODO: Route - POST /auth/refresh
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<AccessTokenResponse>

    // TODO: User logout
    // TODO: Route - POST /auth/logout
    @POST("auth/logout")
    suspend fun logout(
        @Body request: RefreshTokenRequest
    ): Response<Unit>
}
