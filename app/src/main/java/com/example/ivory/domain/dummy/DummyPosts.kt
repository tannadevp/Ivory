package com.example.ivory.domain.dummy

import com.example.ivory.domain.model.ModerationInfo
import com.example.ivory.domain.model.Post

val dummyPosts = listOf(

    Post(
        id = "1",
        username = "Aarav",
        userAvatarUrl = null,
        content = "Had an amazing day at the college fest! 🎉",
        imageUrl = null,
        likeCount = 42,
        commentCount = 8,
        timestamp = System.currentTimeMillis() - 3600000,
        moderation = ModerationInfo(
            toxicityScore = 0.02f,
            ageRating = "Everyone",
            isSensitive = false,
            reason = null
        )
    ),

    Post(
        id = "2",
        username = "Priya",
        userAvatarUrl = null,
        content = "Finally completed my project! Feeling so relieved 😭",
        imageUrl = null,
        likeCount = 87,
        commentCount = 14,
        timestamp = System.currentTimeMillis() - 7200000,
        moderation = ModerationInfo(
            toxicityScore = 0.01f,
            ageRating = "Everyone",
            isSensitive = false,
            reason = null
        )
    ),

    Post(
        id = "3",
        username = "Rahul",
        userAvatarUrl = null,
        content = "Does anyone want to join the coding contest this weekend?",
        imageUrl = null,
        likeCount = 31,
        commentCount = 6,
        timestamp = System.currentTimeMillis() - 10800000,
        moderation = ModerationInfo(
            toxicityScore = 0.03f,
            ageRating = "Everyone",
            isSensitive = false,
            reason = null
        )
    ),

    Post(
        id = "4",
        username = "Ananya",
        userAvatarUrl = null,
        content = "This is a sample post that has been flagged as sensitive.",
        imageUrl = null,
        likeCount = 12,
        commentCount = 3,
        timestamp = System.currentTimeMillis() - 18000000,
        moderation = ModerationInfo(
            toxicityScore = 0.72f,
            ageRating = "18+",
            isSensitive = true,
            reason = "Potentially sensitive content"
        )
    )
)