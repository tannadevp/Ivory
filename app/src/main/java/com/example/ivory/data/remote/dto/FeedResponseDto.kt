package com.example.ivory.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedResponseDto(
    val posts: List<PostDto>,

    @SerialName("next_page")
    val nextPage: Int?
)