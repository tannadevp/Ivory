package com.example.ivory.ui.theme.screen.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.signedInEmail) {
        uiState.signedInEmail?.let { email ->
            if (uiState.requiresEmailVerification) onEmailVerification(email) else onLogIn()
        }
    }

    AuthPage {
        AuthTitle("Welcome back", "Log in to continue to Ivory.")

        Spacer(Modifier.height(24.dp))

        AuthTextField(uiState.email, viewModel::updateEmail, "Email")

        Spacer(Modifier.height(12.dp))

        AuthTextField(
            uiState.password,
            viewModel::updatePassword,
            "Password",
            isPassword = !passwordVisible,
            // If AuthTextField supports a trailing icon slot, wire it up like this:
            // trailingIcon = {
            //     IconButton(onClick = { passwordVisible = !passwordVisible }) {
            //         Icon(
            //             imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
            //             contentDescription = if (passwordVisible) "Hide password" else "Show password",
            //             tint = Color.White.copy(alpha = 0.6f)
            //         )
            //     }
            // }
        )

        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = uiState.errorMessage.orEmpty(),
                color = Color(0xFFFF8080),
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onForgotPassword) {
                Text("Forgot password?", color = AuthAccent, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = viewModel::logIn,
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AuthAccent,
                disabledContainerColor = AuthAccent.copy(alpha = 0.6f)
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.height(22.dp)
                )
            } else {
                Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("New to Ivory?", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            TextButton(onClick = onSignUp) {
                Text("Create an account", color = AuthAccent, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}