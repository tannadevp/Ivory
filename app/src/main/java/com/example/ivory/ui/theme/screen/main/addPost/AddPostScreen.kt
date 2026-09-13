package com.example.ivory.ui.theme.screen.main.addPost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.ivory.viewModels.AddPostViewModel

@Composable
fun AddPostScreen(viewModel: AddPostViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .padding(16.dp)
    ) {
        Text(
            "New Post",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.text,
            onValueChange = viewModel::onTextChanged,
            placeholder = { Text("What's on your mind?", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF6C4DFF),
                unfocusedBorderColor = Color.DarkGray
            ),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = if (uiState.result == null) viewModel::checkPost else viewModel::publishPost,
            enabled = uiState.text.isNotBlank() && !uiState.isLoading && !uiState.isPosted,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4DFF))
        ) {
            Text(
                when {
                    uiState.isLoading -> "Checking content..."
                    uiState.isPosted -> "Posted to Home"
                    uiState.result != null -> "Post content"
                    else -> "Check content"
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = Color(0xFF6C4DFF))
        }

        uiState.result?.let { result ->
            AnalysisResultCard(result)
        }

        if (uiState.isPosted) {
            Spacer(Modifier.height(12.dp))
            Text("Your post is now at the top of the Home feed.", color = Color(0xFF71D68A))
        }

        uiState.error?.let { error ->
            Text(error, color = Color(0xFFFF4D6D))
        }
    }
}
