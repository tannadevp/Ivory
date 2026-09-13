package com.example.ivory.domain.repository

import com.example.ivory.domain.model.Post
import com.example.ivory.domain.dummy.dummyPosts
import com.example.ivory.data.remote.dto.ModerationResponseDto
import com.example.ivory.data.remote.dto.RatingDto
import com.example.ivory.domain.dummy.dummyPosts


class FakeFeedRepository : FeedRepository {

    override suspend fun getFeed(
        page: Int,
        limit: Int
    ): Result<List<Post>> {

        return Result.success(dummyPosts)
    }

    override suspend fun moderatePost(
        content: String
    ): Result<ModerationResponseDto> {

        val isBlocked = content.contains("toxic", ignoreCase = true)

        val isSensitive =
            isBlocked ||
                    content.contains("sensitive", ignoreCase = true)

        val toxicity = when {
            isBlocked -> 0.96
            isSensitive -> 0.50
            else -> 0.10
        }

        return Result.success(
            ModerationResponseDto(

                // Required by the new DTO
                inputText = content,

                overallToxicity = toxicity,

                anyFlagged = isBlocked,

                rating = RatingDto(
                    ageRating = if (isSensitive) "13+" else "All ages",

                    isSensitive = isSensitive,

                    toxicityRating = when {
                        isBlocked -> "high"
                        isSensitive -> "moderate"
                        else -> "low"
                    },

                    message = if (isSensitive) {
                        "This post may need review."
                    } else {
                        "This post looks safe."
                    },

                    blurLevel = if (isSensitive) 1 else 0,

                    warningTitle = if (isSensitive) {
                        "Content warning"
                    } else {
                        "No warning"
                    },

                    warningReason = when {
                        isBlocked -> "community_guidelines_violation"
                        isSensitive -> "sensitive_content"
                        else -> ""
                    },

                    ageRatingLevel = if (isSensitive) {
                        "moderate"
                    } else {
                        "none"
                    }
                ),

                // Fake repository doesn't generate suggestions
                suggestion = null
            )
        )
    }
}
