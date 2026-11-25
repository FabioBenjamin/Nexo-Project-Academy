package com.nexo.data.api;

import com.nexo.dto.UserAchievementDTO;

import java.util.List;

import kotlin.Unit;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface AchievementApiService {

    // List of user achievements with unlock status
    @GET("api/achievements")
    suspend fun getUserAchievement():
            Response<List<UserAchievementDTO>>

    // Records the completion of 1 active/topic
    @POST("api/achievements/complete-activity/{userId}")
    suspend fun completeActivity(
        @Path("userId") userId: Long):
            Response<Unit>

    @POST("api/achievements/add-xp/{userId}")
    suspend fun addXp(
        @Path("userId") userId: Long, @Query("xp") xp: Int):
            Response<Unit>

    @POST("api/achievements/complete-trail/{userId}")
    suspend fun completeTrail(
        @Path("userId") userId: Long):
            Response<Unit>
}
