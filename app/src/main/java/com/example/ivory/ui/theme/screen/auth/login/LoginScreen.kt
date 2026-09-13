package com.example.ivory.ui.theme.screen.auth.login

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.unit.dp
import com.example.ivory.ui.theme.screen.auth.AuthAccent
import com.example.ivory.ui.theme.screen.auth.AuthPage
import com.example.ivory.ui.theme.screen.auth.AuthTextField
import com.example.ivory.ui.theme.screen.auth.AuthTitle
import com.example.ivory.viewModels.auth.LoginViewModel

@Composable
fun LoginScreen(
    onLogIn: () -> Unit,
    onEmailVerification: (email: String) -> Unit,
    onForgotPassword: () -> Unit,
    onSignUp: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.signedInEmail) {
        uiState.signedInEmail?.let { email ->
            if (uiState.requiresEmailVerification) onEmailVerification(email) else onLogIn()
        }
    }
    AuthPage {
        AuthTitle("Welcome back", "Log in to continue to Ivory.")
        AuthTextField(uiState.email, viewModel::updateEmail, "Email")
        AuthTextField(uiState.password, viewModel::updatePassword, "Password", isPassword = true)
        uiState.errorMessage?.let { Text(it, color = Color(0xFFFF8080), modifier = Modifier.fillMaxWidth()) }
        TextButton(onClick = onForgotPassword, modifier = Modifier.fillMaxWidth()) { Text("Forgot password?", color = AuthAccent) }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = viewModel::logIn,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AuthAccent)
        ) { if (uiState.isLoading) CircularProgressIndicator(color = Color.White) else Text("Log in") }
        TextButton(onClick = onSignUp) { Text("New to Ivory? Create an account", color = Color.White) }
    }
}
