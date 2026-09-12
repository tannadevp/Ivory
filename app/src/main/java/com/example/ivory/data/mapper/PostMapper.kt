package com.example.ivory.data.mapper

import com.example.ivory.data.remote.dto.PostDto
import com.example.ivory.domain.model.ModerationInfo
import com.example.ivory.domain.model.Post

fun PostDto.toDomain(): Post = Post(
    id = id,
    username = username,
    userAvatarUrl = userAvatarUrl,
    content = content,
    imageUrl = imageUrl,
    likeCount = likeCount,
    commentCount = commentCount,
    timestamp = timestamp,
    moderation = ModerationInfo(
        toxicityScore = toxicityScore ?: 0f,
        ageRating = ageRating,
        isSensitive = isSensitive,
        reason = sensitivityReason
    )
)