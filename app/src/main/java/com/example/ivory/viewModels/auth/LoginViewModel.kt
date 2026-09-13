package com.example.ivory.viewModels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivory.data.firebase.FirebaseAuthProvider
import com.example.ivory.ui.theme.screen.auth.login.LoginScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuthProvider: FirebaseAuthProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) = update { copy(email = email, errorMessage = null) }
    fun updatePassword(password: String) = update { copy(password = password, errorMessage = null) }

    fun logIn() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            update { copy(errorMessage = "Enter your email and password.") }
            return
        }
        viewModelScope.launch {
            update { copy(isLoading = true, errorMessage = null) }
            runCatching {
                val firebaseAuth = firebaseAuthProvider.getOrNull()
                    ?: error("ERROR")
                firebaseAuth.signInWithEmailAndPassword(state.email, state.password).await().user
            }
                .onSuccess { user ->
                    if (user == null) update { copy(isLoading = false, errorMessage = "Unable to sign in.") }
                    else update {
                        copy(
                            isLoading = false,
                            signedInEmail = user.email ?: state.email,
                            requiresEmailVerification = !user.isEmailVerified
                        )
                    }
                }
                .onFailure { error -> update { copy(isLoading = false, errorMessage = error.message ?: "Unable to sign in.") } }
        }
    }

    private fun update(transform: LoginScreenUiState.() -> LoginScreenUiState) {
        _uiState.value = _uiState.value.transform()
    }
}
