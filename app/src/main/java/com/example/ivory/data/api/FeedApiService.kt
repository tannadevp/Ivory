package com.example.ivory.data.api

import com.example.ivory.data.remote.dto.FeedResponseDto
import com.example.ivory.data.remote.dto.ModerationDto
import com.example.ivory.data.remote.dto.ModerationResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FeedApiService {
    @GET("feed")
    suspend fun getFeed(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20
    ): Response<FeedResponseDto>
    @POST("moderate")
    suspend fun moderatePost(
        @Body request: ModerationDto
    ): Response<ModerationResponseDto>
}

