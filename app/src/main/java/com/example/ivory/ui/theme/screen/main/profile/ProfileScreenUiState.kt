package com.example.ivory.ui.theme.screen.main.profile

data class ProfileScreenUiState(
    val name: String = "",
    val username: String = "",
    val age: String = "",
    val dateOfBirth: String = "",
    val message: String? = null,
    val isSaved: Boolean = false
)
