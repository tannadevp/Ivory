package com.example.ivory.viewModels.auth

import androidx.lifecycle.ViewModel
import com.example.ivory.ui.theme.screen.auth.emailVerified.EmailVerifiedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EmailVerifiedViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(EmailVerifiedUiState(email = ""))
    val uiState: StateFlow<EmailVerifiedUiState> = _uiState.asStateFlow()

    fun setEmail(email: String) {
        if (_uiState.value.email != email) _uiState.value = EmailVerifiedUiState(email)
    }
}
