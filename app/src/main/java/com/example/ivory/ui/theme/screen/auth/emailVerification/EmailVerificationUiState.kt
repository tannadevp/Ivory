package com.example.ivory.ui.theme.screen.auth.emailVerification

data class EmailVerificationUiState(
    val email: String,
    val resendMessage: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isVerified: Boolean = false
)
