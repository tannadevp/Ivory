package com.example.ivory.data.repository

import com.example.ivory.data.remote.dto.ModerationResponseDto
import com.example.ivory.domain.dummy.dummyPosts
import com.example.ivory.domain.model.ModerationInfo
import com.example.ivory.domain.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** In-memory feed shared by Home and Add Post until a posts backend is added. */
@Singleton
class FeedStore @Inject constructor() {
    private val _posts = MutableStateFlow(dummyPosts)
    val posts = _posts.asStateFlow()

    fun replacePosts(posts: List<Post>) {
        _posts.value = posts
    }

    fun publish(content: String, result: ModerationResponseDto) {
        val post = Post(
            id = UUID.randomUUID().toString(),
            username = "You",
            userAvatarUrl = null,
            content = content,
            imageUrl = null,
            likeCount = 0,
            commentCount = 0,
            timestamp = System.currentTimeMillis(),
            moderation = ModerationInfo(
                toxicityScore = result.overallToxicity,
                ageRating = result.ageRating,
                isSensitive = result.isSensitive || result.anyFlagged,
                reason = result.warningReason.takeIf { it.isNotBlank() }
            )
        )
        _posts.value = listOf(post) + _posts.value
    }
}
