package com.example.ivory.ui.theme.screen.auth.getDetails

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ivory.ui.theme.screen.auth.AuthAccent
import com.example.ivory.ui.theme.screen.auth.AuthPage
import com.example.ivory.ui.theme.screen.auth.AuthTextField
import com.example.ivory.ui.theme.screen.auth.AuthTitle
import com.example.ivory.viewModels.main.ProfileViewModel

@Composable
fun GetDetailsScreen(
    onComplete: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onComplete()
    }

    AuthPage {
        AuthTitle("Tell us about you", "These details will appear on your Ivory profile.")
        AuthTextField(uiState.name, viewModel::updateName, "Name")
        AuthTextField(uiState.username, viewModel::updateUsername, "Username")
        AuthTextField(
            uiState.age,
            viewModel::updateAge,
            "Age",
            keyboardType = KeyboardType.Number
        )
        AuthTextField(uiState.dateOfBirth, viewModel::updateDateOfBirth, "Date of birth (DD/MM/YYYY)")
        uiState.message?.let { message ->
            Text(
                message,
                color = if (uiState.isSaved) Color(0xFF77D98A) else Color(0xFFFF8080),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }
        Button(
            onClick = viewModel::saveProfile,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AuthAccent)
        ) {
            Text("Continue")
        }
    }
}
