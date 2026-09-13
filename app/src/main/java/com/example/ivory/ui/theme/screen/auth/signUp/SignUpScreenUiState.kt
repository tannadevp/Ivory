package com.example.ivory.ui.theme.screen.auth.signUp

data class SignUpScreenUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val registeredEmail: String? = null
)
