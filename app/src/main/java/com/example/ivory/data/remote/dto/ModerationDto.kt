package com.example.ivory.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModerationDto(
    val content: String
)

@Serializable
data class ModerationResponseDto(
    @SerialName("toxicity_score")
    val toxicityScore: Float,

    @SerialName("is_sensitive")
    val isSensitive: Boolean,

    @SerialName("sensitivity_reason")
    val sensitivityReason: String?
)
