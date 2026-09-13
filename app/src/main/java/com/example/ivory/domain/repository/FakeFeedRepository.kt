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
                overallToxicity = when {
                    isBlocked -> 0.96f
                    isSensitive -> 0.5f
                    else -> 0.1f
                },
                anyFlagged = isBlocked,
                isSensitive = isSensitive,
                toxicityRating = if (isBlocked) "high" else if (isSensitive) "moderate" else "low",
                message = if (isSensitive) "This post may need review." else "This post looks safe.",
                blurLevel = if (isSensitive) 1 else 0,
                warningTitle = if (isSensitive) "Content warning" else "No warning",
                warningReason = if (isBlocked) "community_guidelines_violation" else if (isSensitive) "sensitive_content" else "",
                ageRating = if (isSensitive) "13+" else "All ages",
                ageRatingLevel = if (isSensitive) "moderate" else "none"
            )
        )
    }
}
