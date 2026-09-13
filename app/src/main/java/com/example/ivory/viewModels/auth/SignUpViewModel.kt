package com.example.ivory.viewModels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivory.data.firebase.FirebaseAuthProvider
import com.example.ivory.ui.theme.screen.auth.signUp.SignUpScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpScreenUiState())
    val uiState: StateFlow<SignUpScreenUiState> = _uiState.asStateFlow()

    fun updateName(name: String) = update { copy(name = name, errorMessage = null) }
    fun updateEmail(email: String) = update { copy(email = email, errorMessage = null) }
    fun updatePassword(password: String) = update { copy(password = password, errorMessage = null) }
    fun updateConfirmPassword(password: String) = update { copy(confirmPassword = password, errorMessage = null) }

    fun createAccount() {
        val state = _uiState.value
        val error = when {
            state.name.isBlank() || state.email.isBlank() || state.password.isBlank() -> "Complete all fields."
            state.password.length < 6 -> "Password must be at least 6 characters."
            state.password != state.confirmPassword -> "Passwords do not match."
            else -> null
        }
        if (error != null) {
            update { copy(errorMessage = error) }
            return
        }
        viewModelScope.launch {
            update { copy(isLoading = true, errorMessage = null) }
            runCatching {
                val firebaseAuth = firebaseAuthProvider.getOrNull()
                    ?: error("ERROR")
                val user = firebaseAuth.createUserWithEmailAndPassword(state.email, state.password).await().user
                    ?: error("Unable to create account.")
                user.sendEmailVerification().await()
                user.email ?: state.email
            }.onSuccess { email ->
                update { copy(isLoading = false, registeredEmail = email) }
            }.onFailure { error ->
                update { copy(isLoading = false, errorMessage = error.message ?: "Unable to create account.") }
            }
        }
    }

    private fun update(transform: SignUpScreenUiState.() -> SignUpScreenUiState) {
        _uiState.value = _uiState.value.transform()
    }
}
