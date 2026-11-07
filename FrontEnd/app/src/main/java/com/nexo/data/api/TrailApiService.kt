package com.nexo.data.api

import com.nexo.dto.StudyTrailRequestDTO
import com.nexo.dto.StudyTrailSummaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TrailApiService {

    // TODO: Lists the user's study tracks (Paginated)
    // TODO: Route: GET /trails?page=0
    @GET("trails")
    suspend fun getTrails(
        @Query("page") page: Int = 0
    ): Response<List<StudyTrailSummaryResponse>>

    // TODO: Generates a new study path based on a request to the AI (Gemini)
    // TODO: Route - POST /trails/generate
    @POST("trails/generate")
    suspend fun generateTrail(
        @Body request: StudyTrailRequestDTO
    ): Response<StudyTrailSummaryResponse>
}