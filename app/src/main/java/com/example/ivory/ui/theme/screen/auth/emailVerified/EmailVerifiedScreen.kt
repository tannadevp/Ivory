package com.example.ivory.ui.theme.screen.auth.emailVerified

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.ivory.ui.theme.screen.auth.AuthAccent
import com.example.ivory.ui.theme.screen.auth.AuthPage
import com.example.ivory.ui.theme.screen.auth.AuthTitle
import com.example.ivory.viewModels.auth.EmailVerifiedViewModel

@Composable
fun EmailVerifiedScreen(
    email: String,
    onContinue: () -> Unit,
    viewModel: EmailVerifiedViewModel = hiltViewModel()
) {
    LaunchedEffect(email) { viewModel.setEmail(email) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AuthPage {
        AuthTitle("Email verified", "${uiState.email} is ready to use.")
        Text("Your account is set up. Welcome to Ivory!", color = Color(0xFF77D98A))
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AuthAccent)
        ) { Text("Continue to Ivory") }
    }
}
