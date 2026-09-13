package com.example.ivory.viewModels.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.ivory.ui.theme.screen.main.profile.ProfileScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val preferences = context.getSharedPreferences("ivory_profile", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        ProfileScreenUiState(
            name = preferences.getString("name", "").orEmpty(),
            username = preferences.getString("username", "").orEmpty(),
            age = preferences.getString("age", "").orEmpty(),
            dateOfBirth = preferences.getString("date_of_birth", "").orEmpty(),
            isSaved = preferences.getBoolean("is_saved", false)
        )
    )
    val uiState = _uiState.asStateFlow()

    fun updateName(value: String) = update { copy(name = value, message = null) }
    fun updateUsername(value: String) = update { copy(username = value, message = null) }
    fun updateAge(value: String) = update { copy(age = value.filter(Char::isDigit), message = null) }
    fun updateDateOfBirth(value: String) = update { copy(dateOfBirth = value, message = null) }

    fun saveProfile() {
        val state = _uiState.value
        if (state.name.isBlank() || state.username.isBlank() || state.age.isBlank() || state.dateOfBirth.isBlank()) {
            update { copy(message = "Please complete all profile fields.") }
            return
        }

        preferences.edit()
            .putString("name", state.name.trim())
            .putString("username", state.username.trim())
            .putString("age", state.age)
            .putString("date_of_birth", state.dateOfBirth.trim())
            .putBoolean("is_saved", true)
            .apply()
        update { copy(isSaved = true, message = "Profile saved.") }
    }

    private fun update(transform: ProfileScreenUiState.() -> ProfileScreenUiState) {
        _uiState.value = _uiState.value.transform()
    }
}
