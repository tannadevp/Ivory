package com.example.ivory.viewModels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivory.domain.dummy.dummyPosts
import com.example.ivory.domain.repository.FeedRepository
import com.example.ivory.ui.theme.screen.main.home.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: FeedRepository = com.example.ivory.domain.repository.FakeFeedRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            repository.getFeed(page = 1)
                .onSuccess { posts ->

                    _uiState.value = HomeUiState(
                        posts = posts,
                        isLoading = false
                    )
                }
                .onFailure { error ->

                    _uiState.value = HomeUiState(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }
}