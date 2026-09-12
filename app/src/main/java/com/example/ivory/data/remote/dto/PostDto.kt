package com.example.ivory.data.remote.dto

data class PostDto(
    val id: String,
    val username: String,
    @SerializedName("user_avatar_url") val userAvatarUrl: String?,
    val content: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("like_count") val likeCount: Int,
    @SerializedName("comment_count") val commentCount: Int,
    val timestamp: Long,
    @SerializedName("toxicity_score") val toxicityScore: Float,
    @SerializedName("age_rating") val ageRating: String,
    @SerializedName("is_sensitive") val isSensitive: Boolean,
    @SerializedName("sensitivity_reason") val sensitivityReason: String?
)