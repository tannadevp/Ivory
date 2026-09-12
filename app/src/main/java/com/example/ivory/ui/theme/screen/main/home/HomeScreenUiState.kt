package com.example.ivory.ui.theme.screen.main.home

import com.example.ivory.domain.model.Post

data class HomeUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)