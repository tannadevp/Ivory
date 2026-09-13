package com.example.ivory.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivory.data.remote.dto.ModerationResponseDto
import com.example.ivory.data.repository.FeedStore
import com.example.ivory.data.repository.ModerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddPostUiState(
    val text: String = "",
    val isLoading: Boolean = false,
    val result: ModerationResponseDto? = null,
    val error: String? = null,
    val isPosted: Boolean = false
)

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val repository: ModerationRepository,
    private val feedStore: FeedStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPostUiState())
    val uiState: StateFlow<AddPostUiState> = _uiState.asStateFlow()

    fun onTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(
            text = text,
            // A previous analysis no longer describes the edited post.
            result = null,
            error = null,
            isPosted = false
        )
    }

    fun checkPost() {

        val text = _uiState.value.text.trim()

        if (text.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter something"
            )
            return
        }

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                result = null
            )

            try {

                val response = repository.moderatePost(text)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    result = response
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Something went wrong"
                )
            }
        }
    }

    fun publishPost() {
        val result = _uiState.value.result ?: return
        val text = _uiState.value.text.trim()
        if (text.isBlank()) return

        feedStore.publish(text, result)
        _uiState.value = _uiState.value.copy(isPosted = true)
    }
    fun useSuggestion() {
        val suggestion = _uiState.value.result
            ?.suggestion
            ?.suggestedRewrite
            ?: return

        _uiState.value = _uiState.value.copy(
            text = suggestion,
            result = null,
            error = null
        )
    }
}
