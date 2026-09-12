package com.example.ivory.viewModels.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivory.domain.repository.FeedRepository
import com.example.ivory.domain.repository.FakeFeedRepository
import com.example.ivory.ui.theme.screen.main.addPost.AddPostUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddPostViewModel(
    private val repo: FeedRepository = FakeFeedRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddPostUiState>(AddPostUiState.Idle)
    val uiState: StateFlow<AddPostUiState> = _uiState

    var content by mutableStateOf("")
        private set

    fun onContentChanged(newContent: String) {
        content = newContent
        // Reset the moderation result if the user edits after seeing it
        if (_uiState.value is AddPostUiState.Checked) {
            _uiState.value = AddPostUiState.Idle
        }
    }

    fun checkContent() {
        if (content.isBlank()) return
        viewModelScope.launch {
            _uiState.value = AddPostUiState.Checking
            repo.moderatePost(content)
                .onSuccess { _uiState.value = AddPostUiState.Checked(it) }
                .onFailure { _uiState.value = AddPostUiState.Error(it.message ?: "Failed to check content") }
        }
    }

    fun publishPost() {
        val current = _uiState.value
        if (current !is AddPostUiState.Checked) return
        if (current.result.toxicityScore > 0.95f) {
            _uiState.value = AddPostUiState.Error(
                "This content violates community guidelines and can't be posted."
            )
            return
        }

        viewModelScope.launch {
            // TODO: replace with a real publish/createPost API call once your
            // friend adds one. For now this just simulates success so you can
            // test the full UI flow with dummy posts.
            _uiState.value = AddPostUiState.Posted
        }
    }

    fun reset() {
        content = ""
        _uiState.value = AddPostUiState.Idle
    }
}
