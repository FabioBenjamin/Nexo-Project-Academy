package com.example.nexoacademy.data.api

import android.content.Context
import com.example.nexoacademy.data.local.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // 💡 IMPORTANTE: Emuladores Android usam 10.0.2.2 para acessar o localhost da máquina hospedeira (Spring Boot na porta 8080)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit {
        if (retrofit == null) {
            val tokenManager = TokenManager(context.applicationContext)

            // Logging Interceptor para visualizar as requisições no Logcat do Android Studio
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // OkHttpClient configurado com Timeouts e Interceptor JWT
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenManager))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
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

    // Instância pronta do serviço de Autenticação
    fun getAuthApiService(context: Context): AuthApiService {
        return getClient(context).create(AuthApiService::class.java)
    }

    fun getTrailApiService(context: Context): TrailApiService = getClient(context).create(TrailApiService::class.java)
}