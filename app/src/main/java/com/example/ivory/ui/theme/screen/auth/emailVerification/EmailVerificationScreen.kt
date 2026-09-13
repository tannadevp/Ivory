package com.example.ivory.ui.theme.screen.auth.emailVerification

import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.ivory.ui.theme.screen.auth.AuthAccent
import com.example.ivory.ui.theme.screen.auth.AuthPage
import com.example.ivory.ui.theme.screen.auth.AuthTitle
import com.example.ivory.viewModels.auth.EmailVerificationViewModel

@Composable
fun EmailVerificationScreen(
    email: String,
    onVerified: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: EmailVerificationViewModel = hiltViewModel()
) {
    LaunchedEffect(email) { viewModel.setEmail(email) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified) onVerified()
    }
    AuthPage {
        AuthTitle("Verify your email", "We sent a verification link to")
        Text(uiState.email, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        uiState.resendMessage?.let { Text(it, color = Color(0xFF77D98A), modifier = Modifier.fillMaxWidth()) }
        uiState.errorMessage?.let { Text(it, color = Color(0xFFFF8080), modifier = Modifier.fillMaxWidth()) }
        Button(
            onClick = viewModel::checkVerification,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AuthAccent)
        ) { if (uiState.isLoading) CircularProgressIndicator(color = Color.White) else Text("I've verified my email") }
        TextButton(onClick = viewModel::resendEmail, enabled = !uiState.isLoading) { Text("Resend email", color = AuthAccent) }
        TextButton(onClick = onBackToLogin) { Text("Back to log in", color = Color.White) }
    }
}
