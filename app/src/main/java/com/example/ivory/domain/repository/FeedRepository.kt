package com.example.ivory.domain.repository

import com.example.ivory.data.remote.dto.ModerationResponseDto
import com.example.ivory.domain.model.Post

// domain/repository/FeedRepository.kt
interface FeedRepository {
    suspend fun getFeed(page: Int, limit: Int = 20): Result<List<Post>>
    suspend fun moderatePost(content: String): Result<ModerationResponseDto>
}
