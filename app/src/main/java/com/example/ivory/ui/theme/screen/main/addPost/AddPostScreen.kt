package com.example.ivory.ui.theme.screen.main.addPost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ivory.viewModels.main.AddPostViewModel

@Composable
fun AddPostScreen(viewModel: AddPostViewModel = viewModel()) {
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
            value = viewModel.content,
            onValueChange = viewModel::onContentChanged,
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

        when (val state = uiState) {
            is AddPostUiState.Idle -> {
                Button(
                    onClick = viewModel::checkContent,
                    enabled = viewModel.content.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4DFF))
                ) {
                    Text("Check & Continue")
                }
            }

            is AddPostUiState.Checking -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6C4DFF))
                    Spacer(Modifier.width(10.dp))
                    Text("Checking content...", color = Color.Gray)
                }
            }

            is AddPostUiState.Checked -> {
                AnalysisResultCard(state.result)
                Spacer(Modifier.height(12.dp))
                Row {
                    OutlinedButton(
                        onClick = viewModel::reset,
                        modifier = Modifier.weight(1f)
                    ) { Text("Edit") }

                    Spacer(Modifier.width(10.dp))

                    Button(
                        onClick = viewModel::publishPost,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4DFF))
                    ) { Text("Post Anyway") }
                }
            }

            is AddPostUiState.Posted -> {
                LaunchedEffect(Unit) {
                    viewModel.reset()
                }
                Text("Posted!", color = Color.Green)
            }

            is AddPostUiState.Error -> {
                Text(state.message, color = Color.Red)
                Spacer(Modifier.height(8.dp))
                Button(onClick = viewModel::checkContent) { Text("Retry") }
            }
        }
    }
}
