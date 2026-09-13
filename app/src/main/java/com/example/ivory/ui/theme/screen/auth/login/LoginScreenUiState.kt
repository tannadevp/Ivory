package com.example.ivory.ui.theme.screen.auth.login

data class LoginScreenUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val signedInEmail: String? = null,
    val requiresEmailVerification: Boolean = false
)
