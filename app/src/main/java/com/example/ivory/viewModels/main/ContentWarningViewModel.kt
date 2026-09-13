package com.example.ivory.viewModels.main

import androidx.lifecycle.ViewModel
import com.example.ivory.data.repository.FeedStore
import com.example.ivory.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContentWarningViewModel @Inject constructor(
    private val feedStore: FeedStore
) : ViewModel() {
    fun findPost(postId: String): Post? = feedStore.posts.value.firstOrNull { it.id == postId }
}
