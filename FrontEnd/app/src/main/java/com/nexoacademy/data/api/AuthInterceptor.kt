package com.example.nexoacademy.data.api

import com.example.nexoacademy.data.local.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Rotas públicas de autenticação não precisam do cabeçalho de Token de Acesso
        val urlPath = originalRequest.url.encodedPath
        if (urlPath.contains("auth/login") || urlPath.contains("auth/register")) {
            return chain.proceed(originalRequest)
        }

        // Obtém o Access Token atual de forma síncrona dentro do Interceptor
        val token = runBlocking { tokenManager.accessToken.firstOrNull() }

        // Se existir o token, injeta no cabeçalho Authorization: Bearer <token>
        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}