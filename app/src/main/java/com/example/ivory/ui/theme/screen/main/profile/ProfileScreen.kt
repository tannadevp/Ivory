package com.example.ivory.ui.theme.screen.main.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    var name by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .padding(16.dp)
    ) {
        Text("Create profile", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        Text("Add your details to finish setting up Ivory.", color = Color.Gray, fontSize = 14.sp)
        Spacer(Modifier.height(20.dp))

        ProfileTextField(name, { name = it; message = null }, "Name")
        ProfileTextField(username, { username = it; message = null }, "Username")
        ProfileTextField(
            value = age,
            onValueChange = { age = it.filter(Char::isDigit); message = null },
            label = "Age",
            keyboardType = KeyboardType.Number
        )
        ProfileTextField(dateOfBirth, { dateOfBirth = it; message = null }, "Date of birth (DD/MM/YYYY)")

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                message = if (name.isBlank() || username.isBlank() || age.isBlank() || dateOfBirth.isBlank()) {
                    "Please complete all profile fields."
                } else {
                    "Profile created."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4DFF))
        ) {
            Text("Create profile")
        }
        message?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = if (it == "Profile created.") Color(0xFF71D68A) else Color(0xFFFF8080))
        }
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
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF6C4DFF),
            unfocusedBorderColor = Color.DarkGray,
            focusedLabelColor = Color(0xFFB6AAFF),
            unfocusedLabelColor = Color.Gray
        )
    )
    Spacer(Modifier.height(12.dp))
}
