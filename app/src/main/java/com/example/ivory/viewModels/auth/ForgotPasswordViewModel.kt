package com.example.ivory.viewModels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivory.data.firebase.FirebaseAuthProvider
import com.example.ivory.ui.theme.screen.auth.forgotPassword.ForgotPasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null, isEmailSent = false)
    }

    fun sendResetLink() {
        val email = _uiState.value.email
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Enter your email address.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                val firebaseAuth = firebaseAuthProvider.getOrNull()
                    ?: error("ERROR")
                firebaseAuth.sendPasswordResetEmail(email).await()
            }
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, isEmailSent = true) }
                .onFailure { error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "Unable to send reset link.") }
        }
    }
}
