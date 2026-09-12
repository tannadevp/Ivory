package com.example.ivory.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: String,
    val username: String,

    @SerialName("user_avatar_url")
    val userAvatarUrl: String,

    val content: String,

    @SerialName("image_url")
    val imageUrl: String?,

    @SerialName("ml_tag")
    val mlTag: String?,

    @SerialName("like_count")
    val likeCount: Int,

    @SerialName("comment_count")
    val commentCount: Int,

    @SerialName("is_liked")
    val isLiked: Boolean,

    @SerialName("toxicity_score")
    val toxicityScore: Float?,

    @SerialName("age_rating")
    val ageRating: String,

    @SerialName("is_sensitive")
    val isSensitive: Boolean,

    @SerialName("sensitivity_reason")
    val sensitivityReason: String?,

    val timestamp: Long
)