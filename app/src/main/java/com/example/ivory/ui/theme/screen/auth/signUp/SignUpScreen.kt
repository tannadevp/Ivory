package com.example.ivory.ui.theme.screen.auth.signUp

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.ivory.ui.theme.screen.auth.AuthAccent
import com.example.ivory.ui.theme.screen.auth.AuthPage
import com.example.ivory.ui.theme.screen.auth.AuthTextField
import com.example.ivory.ui.theme.screen.auth.AuthTitle
import com.example.ivory.viewModels.auth.SignUpViewModel


@Composable
fun SignUpScreen(
    onSignUp: (email: String) -> Unit,
    onLogIn: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.registeredEmail) {
        uiState.registeredEmail?.let(onSignUp)
    }

    AuthPage {
        AuthTitle(
            "Create your account",
            "Start sharing with more intention."
        )

        AuthTextField(
            uiState.name,
            viewModel::updateName,
            "Name"
        )

        AuthTextField(
            uiState.email,
            viewModel::updateEmail,
            "Email"
        )

        AuthTextField(
            uiState.password,
            viewModel::updatePassword,
            "Password",
            isPassword = true
        )

        AuthTextField(
            uiState.confirmPassword,
            viewModel::updateConfirmPassword,
            "Confirm password",
            isPassword = true
        )

        uiState.errorMessage?.let {
            Text(
                it,
                color = Color(0xFFFF8080),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = viewModel::createAccount,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B5CF6)
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White
                )
            } else {
                Text(
                    "Create account",
                    color = Color.White
                )
            }
        }

        TextButton(
            onClick = onLogIn
        ) {
            Text(
                "Already have an account? Log in",
                color = Color(0xFFA78BFA)
            )
        }
    }
}


