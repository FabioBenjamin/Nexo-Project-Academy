package com.nexo.data.api

import com.nexo.dto.StudyTestResponse
import com.nexo.dto.TestAttemptResultRequest
import com.nexo.dto.TestAttemptResultResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StudyTestApiService {

    @GET("tests/{id}")
    suspend fun getStudyTest(
        @Path("id") testId: Long
    ): Response<StudyTestResponse>

    @POST("tests/{id}/attempts")
    suspend fun submitTestAttempt(
        @Path("id") testId: Long,
        @Body request: TestAttemptResultRequest
    ): Response<TestAttemptResultResponse>
}