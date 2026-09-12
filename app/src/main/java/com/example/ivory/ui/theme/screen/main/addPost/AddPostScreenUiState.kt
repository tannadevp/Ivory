package com.example.ivory.ui.theme.screen.main.addPost

import com.example.ivory.data.remote.dto.ModerationResponseDto

sealed interface AddPostUiState {
    data object Idle : AddPostUiState
    data object Checking : AddPostUiState
    data class Checked(val result: ModerationResponseDto) : AddPostUiState
    data object Posted : AddPostUiState
    data class Error(val message: String) : AddPostUiState
}
