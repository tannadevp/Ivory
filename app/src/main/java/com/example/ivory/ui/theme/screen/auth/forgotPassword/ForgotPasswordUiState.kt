package com.example.ivory.ui.theme.screen.auth.forgotPassword

data class ForgotPasswordUiState(
    val email: String = "",
    val isEmailSent: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)
