package com.nexo.data.api

import com.nexo.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserApiService {

      // Fetches the profile data for the currently authenticated user
      // Endpoint: GET /user/profile

    @GET("user/profile")
    suspend fun getUserProfile(): Response<UserProfileResponse>
}