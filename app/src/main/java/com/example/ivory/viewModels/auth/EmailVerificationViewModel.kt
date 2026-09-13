package com.example.ivory.viewModels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivory.data.firebase.FirebaseAuthProvider
import com.example.ivory.ui.theme.screen.auth.emailVerification.EmailVerificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(EmailVerificationUiState(email = ""))
    val uiState: StateFlow<EmailVerificationUiState> = _uiState.asStateFlow()

    fun setEmail(email: String) {
        if (_uiState.value.email != email) _uiState.value = EmailVerificationUiState(email)
    }

    fun resendEmail() {
        val user = firebaseAuthProvider.getOrNull()?.currentUser
        if (user == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Sign in again to resend the verification email.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, resendMessage = null)
            runCatching { user.sendEmailVerification().await() }
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, resendMessage = "Verification email sent again.") }
                .onFailure { error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "Unable to resend email.") }
        }
    }

    fun checkVerification() {
        val firebaseAuth = firebaseAuthProvider.getOrNull()
        val user = firebaseAuth?.currentUser
        if (user == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Sign in again to verify your email.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                user.reload().await()
                firebaseAuth?.currentUser?.isEmailVerified == true
            }.onSuccess { isVerified ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isVerified = isVerified,
                    errorMessage = if (isVerified) null else "Your email is not verified yet."
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "Unable to check verification.")
            }
        }
    }
}
