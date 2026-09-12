package com.example.ivory.domain.repository

import com.example.ivory.domain.model.Post
import com.example.ivory.domain.dummy.dummyPosts
import com.example.ivory.data.remote.dto.ModerationResponseDto

class FakeFeedRepository : FeedRepository {

    override suspend fun getFeed(
        page: Int,
        limit: Int
    ): Result<List<Post>> {

        return Result.success(dummyPosts)
    }

    override suspend fun moderatePost(content: String): Result<ModerationResponseDto> {
        val isBlocked = content.contains("toxic", ignoreCase = true)
        val isSensitive = isBlocked || content.contains("sensitive", ignoreCase = true)
        return Result.success(
            ModerationResponseDto(
                toxicityScore = when {
                    isBlocked -> 0.96f
                    isSensitive -> 0.5f
                    else -> 0.1f
                },
                isSensitive = isSensitive,
                sensitivityReason = when {
                    isBlocked -> "community_guidelines_violation"
                    isSensitive -> "sensitive_content"
                    else -> null
                }
            )
        )
    }
}
