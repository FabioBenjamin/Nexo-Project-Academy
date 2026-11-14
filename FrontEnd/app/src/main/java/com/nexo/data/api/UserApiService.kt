package com.nexo.data.api

import com.nexo.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserApiService {

      // Fetches the profile data for the currently authenticated user
      // Endpoint: GET /user/profile

    @GET("user/profile")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    // Updates the profile data for the currently authenticated user
    // Endpoint: PUT /user/profile
    //SUPUS A ROTA, REVER SE FUNCIONA
    @PUT("user/profile")
    suspend fun updateProfile(profile: UserProfileResponse): Response<UserProfileResponse>
}