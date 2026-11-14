package com.nexo.dto

// Registration DTO sent to POST /auth/register
data class RegisterUserDTO(
    val username: String,
    val email: String,
    val password: String
)

// Login DTO sent to POST /auth/login
data class LoginUserDTO(
    val email: String,
    val password: String
)

// Response received from the POST /auth/login
data class LoginResponseDTO(
    val accessToken: String,
    val refreshToken: String
)

// Request DTO for Refresh Token and Logout
data class RefreshTokenRequest(
    val refreshToken: String
)

// Token renewal response
data class AccessTokenResponse(
    val accessToken: String
)

// Standard API error response (GlobalExceptionHandler)
data class ErrorResponseDTO(
    val error: String,
    val message: String,
    val status: Int
)