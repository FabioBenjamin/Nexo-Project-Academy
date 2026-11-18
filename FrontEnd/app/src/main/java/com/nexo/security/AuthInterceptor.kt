package com.nexo.security

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // Do not send old tokens in public authentication routes
        if (path.contains("/auth/login") || path.contains("/auth/register") || path.contains("/auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        val requestBuilder = originalRequest.newBuilder()

        // Retrieve token from fast in-memory cache instead of blocking the thread
        val token = tokenManager.cachedAccessToken

        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}