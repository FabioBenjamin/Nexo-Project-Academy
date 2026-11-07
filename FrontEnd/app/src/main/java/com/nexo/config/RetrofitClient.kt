package com.nexo.config

import android.content.Context
import com.nexo.security.AuthInterceptor
import com.nexo.data.api.TrailApiService
import com.nexo.security.TokenManager
import com.nexo.data.api.AuthApiService
import com.nexo.data.api.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // IMPORTANT
    private const val BASE_URL = "http://localhost:8080/"

    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit {
        if (retrofit == null) {
            val tokenManager = TokenManager(context.applicationContext)

            // Logging Interceptor para visualizar as requisições no Logcat do Android Studio
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // OkHttpClient configured with Timeouts and a JWT Interceptor
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenManager))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    // Ready to use instance of the Authentication Service
    fun getAuthApiService(context: Context): AuthApiService {
        return getClient(context).create(AuthApiService::class.java)
    }

    fun getTrailApiService(context: Context): TrailApiService = getClient(context).create(
        TrailApiService::class.java)


    fun getUserApiService(context: Context): UserApiService = getClient(context).create(
        UserApiService::class.java)}
