package com.example.ivory.data.api

import com.example.ivory.data.remote.dto.ModerationDto
import com.example.ivory.data.remote.dto.ModerationResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ModerationApi {

    @POST("classify")
    suspend fun classify(
        @Body request: ModerationDto
    ): ModerationResponseDto
}

