package com.example.nexoacademy.data.api

import com.example.nexoacademy.data.dto.StudyTrailRequestDTO
import com.example.nexoacademy.data.dto.StudyTrailSummaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TrailApiService {

    /**
     * Lista as trilhas de estudo do usuário (Paginado)
     * Rota: GET /trails?page=0
     */
    @GET("trails")
    suspend fun getTrails(
        @Query("page") page: Int = 0
    ): Response<List<StudyTrailSummaryResponse>>

    /**
     * Gera uma nova trilha de estudo a partir da solicitação com a IA (Gemini)
     * Rota: POST /trails/generate
     */
    @POST("trails/generate")
    suspend fun generateTrail(
        @Body request: StudyTrailRequestDTO
    ): Response<StudyTrailSummaryResponse>
}