package com.nexo.config

import android.content.Context
import com.nexo.data.api.AchievementApiService
import com.nexo.data.api.AuthApiService
import com.nexo.data.api.StudyTestApiService
import com.nexo.data.api.TrailApiService
import com.nexo.data.api.UserApiService
import com.nexo.security.AuthInterceptor
import com.nexo.security.TokenAuthenticator
import com.nexo.security.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://nexo-project-academy.onrender.com"

    @Volatile
    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: buildRetrofit(context.applicationContext).also { retrofit = it }
        }
    }

    private fun buildRetrofit(appContext: Context): Retrofit {
        val tokenManager = TokenManager(appContext)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Instance dedicated solely to the refresh token (prevents an infinite loop on 401 errors)
        val refreshAuthService: AuthApiService by lazy {
            val cleanClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(cleanClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthApiService::class.java)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(tokenManager) { refreshAuthService })
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAuthApiService(context: Context): AuthApiService =
        getClient(context).create(AuthApiService::class.java)

    fun getTrailApiService(context: Context): TrailApiService =
        getClient(context).create(TrailApiService::class.java)

    fun getUserApiService(context: Context): UserApiService =
        getClient(context).create(UserApiService::class.java)

    fun getAchievementApiService(context: Context): AchievementApiService =
        getClient(context).create(AchievementApiService::class.java)

    fun getStudyTestApiService(context: Context): StudyTestApiService {
        return getClient(context).create(StudyTestApiService::class.java)
    }
}