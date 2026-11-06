package com.example.nexoacademy.data.dto

// DTO de Cadastro enviado ao POST /auth/register
data class RegisterUserDTO(
    val username: String,
    val email: String,
    val password: String
)

// DTO de Login enviado ao POST /auth/login
data class LoginUserDTO(
    val username: String,
    val password: String
)

// Resposta recebida do POST /auth/login
data class LoginResponseDTO(
    val accessToken: String,
    val refreshToken: String
)

// DTO de requisição para Refresh Token e Logout
data class RefreshTokenRequest(
    val refreshToken: String
)

// Resposta da renovação de token
data class AccessTokenResponse(
    val accessToken: String
)

// Resposta de erro padrão da API (GlobalExceptionHandler)
data class ErrorResponseDTO(
    val error: String,
    val message: String,
    val status: Int
)