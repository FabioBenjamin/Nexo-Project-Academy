package com.nexo.security

import com.nexo.dto.RefreshTokenRequest
import com.nexo.data.api.AuthApiService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val authProvider: () -> AuthApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            val currentToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val storedToken = runBlocking { tokenManager.accessToken.firstOrNull() }

            if (storedToken != null && currentToken != storedToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $storedToken")
                    .build()
            }

            val refreshToken = runBlocking { tokenManager.refreshToken.firstOrNull() }
            if (refreshToken.isNullOrBlank()) {
                runBlocking { tokenManager.clearTokens() }
                return null
            }

            return try {
                val refreshResponse = runBlocking {
                    authProvider().refreshToken(RefreshTokenRequest(refreshToken))
                }

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val newAccessToken = refreshResponse.body()!!.accessToken
                    runBlocking { tokenManager.saveTokens(access = newAccessToken, refresh = refreshToken) }

                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    runBlocking { tokenManager.clearTokens() }
                    null
                }
            } catch (e: Exception) {
                runBlocking { tokenManager.clearTokens() }
                null
            }
        }
    }
}