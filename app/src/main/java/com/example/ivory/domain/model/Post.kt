package com.example.ivory.domain.model

import kotlinx.serialization.Serializable

data class Post(
    val id: String,
    val username: String,
    val userAvatarUrl: String?,
    val content: String,
    val imageUrl: String?,
    val likeCount: Int,
    val commentCount: Int,
    val timestamp: Long,
    val moderation: ModerationInfo
)

data class ModerationInfo(
    val toxicityScore: Float,
    val ageRating: String,
    val isSensitive: Boolean,
    val reason: String?
) {s
    val shouldBlur: Boolean
        get() = isSensitive || toxicityScore > 0.7f
}