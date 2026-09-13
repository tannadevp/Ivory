package com.example.ivory.ui.theme.screen.main.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ivory.viewModels.main.ProfileViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {

        // Header
        Text(
            text = "Your profile",
            color = Color(0xFF1A1A1A),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Add your details to finish setting up Ivory.",
            color = Color(0xFF77727F),
            fontSize = 15.sp,
            lineHeight = 21.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        // Profile fields
        ProfileTextField(
            value = uiState.name,
            onValueChange = viewModel::updateName,
            label = "Name"
        )

        ProfileTextField(
            value = uiState.username,
            onValueChange = viewModel::updateUsername,
            label = "Username"
        )

        ProfileTextField(
            value = uiState.age,
            onValueChange = viewModel::updateAge,
            label = "Age",
            keyboardType = KeyboardType.Number
        )

        ProfileTextField(
            value = uiState.dateOfBirth,
            onValueChange = viewModel::updateDateOfBirth,
            label = "Date of birth (DD/MM/YYYY)"
        )

        Spacer(Modifier.height(8.dp))

        // Save button
        Button(
            onClick = viewModel::saveProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B5CF6)
            )
        ) {
            Text(
                text = if (uiState.isSaved)
                    "Update profile"
                else
                    "Create profile",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Message
        uiState.message?.let {
            Spacer(Modifier.height(12.dp))

            Text(
                text = it,
                color = if (
                    uiState.isSaved &&
                    it == "Profile saved."
                ) {
                    Color(0xFF42A85F)
                } else {
                    Color(0xFFE05252)
                },
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(28.dp))

        // Logout
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF7C3AED)
            ),
            border = BorderStroke(
                1.dp,
                Color(0xFFD8C9FF)
            )
        ) {
            Text(
                text = "Log out",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}


@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        shape = RoundedCornerShape(14.dp),

        colors = OutlinedTextFieldDefaults.colors(
            // Text
            focusedTextColor = Color(0xFF1A1A1A),
            unfocusedTextColor = Color(0xFF1A1A1A),

            // Border
            focusedBorderColor = Color(0xFF8B5CF6),
            unfocusedBorderColor = Color(0xFFD6D1DD),

            // Label
            focusedLabelColor = Color(0xFF7C3AED),
            unfocusedLabelColor = Color(0xFF77727F),

            // Cursor
            cursorColor = Color(0xFF8B5CF6)
        )
    )
}


