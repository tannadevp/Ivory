package com.example.ivory.viewModels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivory.data.repository.FeedStore
import com.example.ivory.data.repository.ModerationRepository
import com.example.ivory.domain.model.ModerationInfo
import com.example.ivory.ui.theme.screen.main.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val moderationRepository: ModerationRepository,
    private val feedStore: FeedStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            feedStore.posts.collect { posts ->
                _uiState.value = _uiState.value.copy(
                    posts = posts,
                    isLoading = false
                )
            }
        }

        loadFeed()
    }

    fun loadFeed() {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val moderatedPosts = feedStore.posts.value.map { post ->

                runCatching {
                    moderationRepository.moderatePost(post.content)
                }
                    .getOrNull()
                    ?.let { result ->

                        post.copy(
                            moderation = ModerationInfo(

                                // Double → Float
                                toxicityScore =
                                    result.overallToxicity.toFloat(),

                                // These are now inside rating
                                ageRating =
                                    result.rating.ageRating,

                                isSensitive =
                                    result.rating.isSensitive ||
                                            result.anyFlagged,

                                reason =
                                    result.rating.warningReason
                                        .takeIf { it.isNotBlank() }
                            )
                        )
                    }
                    ?: post
            }

            feedStore.replacePosts(moderatedPosts)
        }
    }
}
