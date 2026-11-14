package com.nexo.security

import com.nexo.dto.RefreshTokenRequest
import com.nexo.data.api.AuthApiService
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

        // Prevent infinite loop if the refresh route itself returns 401
        if (response.request.url.encodedPath.contains("/refresh")) {
            return null
        }

        synchronized(this) {
            val currentToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            // Read from cache to avoid I/O bottlenecks
            val storedToken = tokenManager.cachedAccessToken

            // If the token was already refreshed by another thread, retry with the new one
            if (storedToken != null && currentToken != storedToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $storedToken")
                    .build()
            }

            // Read refresh token from cache
            val refreshToken = tokenManager.cachedRefreshToken
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

                    // saveTokens will synchronously update the in-memory cache too
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