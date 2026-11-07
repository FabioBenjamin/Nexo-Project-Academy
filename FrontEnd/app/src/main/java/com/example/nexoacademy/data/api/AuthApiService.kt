package com.example.nexoacademy.data.api

import com.example.nexoacademy.data.dto.AccessTokenResponse
import com.example.nexoacademy.data.dto.LoginResponseDTO
import com.example.nexoacademy.data.dto.LoginUserDTO
import com.example.nexoacademy.data.dto.RefreshTokenRequest
import com.example.nexoacademy.data.dto.RegisterUserDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    /**
     * Cadastro de novo usuário
     * Rota: POST /auth/register
     * Retorno esperado: 201 Created (sem corpo)
     */
    @POST("auth/register")
    suspend fun register(
        @Body dto: com.example.nexoacademy.data.dto.RegisterUserDTO
    ): Response<Unit>

    /**
     * Login do usuário
     * Rota: POST /auth/login
     * Retorno esperado: 200 OK com LoginResponseDTO (Tokens)
     */
    @POST("auth/login")
    suspend fun login(
        @Body dto: com.example.nexoacademy.data.dto.LoginUserDTO
    ): Response<com.example.nexoacademy.data.dto.LoginResponseDTO>

    /**
     * Atualizar Token de Acesso (Refresh Token)
     * Rota: POST /auth/refresh
     * Retorno esperado: 200 OK com AccessTokenResponse
     */
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: com.example.nexoacademy.data.dto.RefreshTokenRequest
    ): Response<com.example.nexoacademy.data.dto.AccessTokenResponse>

    /**
     * Logout do utilizador (Invalida o Refresh Token no backend)
     * Rota: POST /auth/logout
     */
    @POST("auth/logout")
    suspend fun logout(
        @Body request: com.example.nexoacademy.data.dto.RefreshTokenRequest
    ): Response<Unit>
}