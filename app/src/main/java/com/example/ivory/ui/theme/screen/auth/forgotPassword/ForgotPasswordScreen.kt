package com.example.ivory.ui.theme.screen.auth.forgotPassword

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.ivory.ui.theme.screen.auth.AuthAccent
import com.example.ivory.ui.theme.screen.auth.AuthPage
import com.example.ivory.ui.theme.screen.auth.AuthTextField
import com.example.ivory.ui.theme.screen.auth.AuthTitle
import com.example.ivory.viewModels.auth.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AuthPage {
        AuthTitle("Reset password", "We'll send a reset link to your email.")
        AuthTextField(uiState.email, viewModel::updateEmail, "Email")
        when {
            uiState.isEmailSent -> Text("Reset link sent. Check your inbox.", color = Color(0xFF77D98A), modifier = Modifier.fillMaxWidth())
            uiState.errorMessage != null -> Text(uiState.errorMessage!!, color = Color(0xFFFF8080), modifier = Modifier.fillMaxWidth())
        }
        Button(
            onClick = {
                viewModel.sendResetLink()
            },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AuthAccent)
        ) { if (uiState.isLoading) CircularProgressIndicator(color = Color.White) else Text("Send reset link") }
        TextButton(onClick = onBackToLogin) { Text("Back to log in", color = Color.White) }
    }
}
