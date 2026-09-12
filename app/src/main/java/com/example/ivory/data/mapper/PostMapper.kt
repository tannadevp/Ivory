package com.example.ivory.data.mapper

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
        toxicityScore = toxicityScore,
        ageRating = ageRating,
        isSensitive = isSensitive,
        reason = sensitivityReason
    )
)