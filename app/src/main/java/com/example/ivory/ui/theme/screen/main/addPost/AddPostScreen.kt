package com.example.ivory.ui.theme.screen.main.addPost

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.ivory.viewModels.AddPostViewModel


@Composable
fun AddPostScreen(
    viewModel: AddPostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3EEFF))
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Create a post",
            color = Color(0xFF35234F),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Share your thoughts safely 💜",
            color = Color(0xFF756681),
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))
        OutlinedTextField(
            value = uiState.text,
            onValueChange = viewModel::onTextChanged,

            placeholder = {
                Text(
                    text = "What's on your mind?",
                    color = Color(0xFF9B8EAA)
                )
            },

            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp),

            textStyle = LocalTextStyle.current.copy(
                color = Color(0xFF35234F),
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),

            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF35234F),
                unfocusedTextColor = Color(0xFF35234F),

                focusedContainerColor = Color(0xFFE9DFFF),
                unfocusedContainerColor = Color(0xFFE9DFFF),

                focusedBorderColor = Color(0xFF6C4AB6),
                unfocusedBorderColor = Color(0xFFD2C1F0),

                cursorColor = Color(0xFF6C4AB6)
            ),

            shape = RoundedCornerShape(20.dp),

            maxLines = 7
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (uiState.result == null) {
                    viewModel.checkPost()
                } else {
                    viewModel.publishPost()
                }
            },

            enabled = uiState.text.isNotBlank()
                    && !uiState.isLoading
                    && !uiState.isPosted,

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),

            shape = RoundedCornerShape(16.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C4AB6),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFD2C1F0),
                disabledContentColor = Color(0xFF8B7A9E)
            )
        ) {
            Text(
                text = when {
                    uiState.isLoading -> "Checking content..."
                    uiState.isPosted -> "Posted ✓"
                    uiState.result != null -> "Post content"
                    else -> "Check content"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (uiState.isLoading) {

            Spacer(Modifier.height(20.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = Color(0xFF6C4AB6),
                strokeWidth = 3.dp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Making sure your post is safe...",
                color = Color(0xFF756681),
                fontSize = 13.sp
            )
        }
        uiState.result?.let { result ->

            Spacer(Modifier.height(20.dp))

            AnalysisResultCard(result)

            if (
                result.anyFlagged &&
                result.suggestion?.suggestedRewrite != null
            ) {

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.useSuggestion()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF6C4AB6)
                    ),
                    border = BorderStroke(
                        1.dp,
                        Color(0xFF6C4AB6)
                    )
                ) {
                    Text(
                        text = "Use safer rewrite",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        if (uiState.isPosted) {

            Spacer(Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFE1F5E7)
            ) {
                Text(
                    text = "✓ Your post is now at the top of the Home feed.",
                    color = Color(0xFF287A45),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        // ───────────── Error ─────────────
        uiState.error?.let { error ->

            Spacer(Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFFE5EB)
            ) {
                Text(
                    text = error,
                    color = Color(0xFFC73555),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

