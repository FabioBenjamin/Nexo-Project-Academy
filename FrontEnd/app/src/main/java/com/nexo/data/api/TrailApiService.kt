package com.nexo.data.api

import com.nexo.dto.PageResponse
import com.nexo.dto.StudyTrailRequestDTO
import com.nexo.dto.StudyTrailResponse
import com.nexo.dto.StudyTrailSummaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TrailApiService {

    /**
     * Fetches paginated study trails for the current user
     * Endpoint: GET /trails?page=0
     */
    @GET("trails")
    suspend fun getTrails(
        @Query("page") page: Int = 0
    ): Response<PageResponse<StudyTrailSummaryResponse>>

    /**
     * Generates a new study trail via Gemini AI
     * Endpoint: POST /trails/generate
     */
    @POST("trail")
    suspend fun generateTrail(
        @Body request: StudyTrailRequestDTO): Response<Long>

    /**
     * Retrieves full trail details with its complete topic hierarchy
     * Endpoint: GET /trail/{id}
     */
    @GET("trail/{id}")
    suspend fun getTrailDetails(
        @Path("id") trailId: Long):
            Response<StudyTrailResponse>

    @POST("api/topics/{topicId}/complete")
    suspend fun completeTopic(@Path("topicId") topicId: Long): Response<Unit>
}