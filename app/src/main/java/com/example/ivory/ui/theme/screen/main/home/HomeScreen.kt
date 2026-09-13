package com.example.ivory.ui.theme.screen.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ivory.viewModels.main.HomeViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onRevealRequested: (String) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F3FF)),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 12.dp,
                        bottom = 8.dp
                    )
            ) {

                Text(
                    text = "Hello  👋",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF35234F),
                    fontFamily = FontFamily.SansSerif
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "What's on your mind today?",
                    fontSize = 15.sp,
                    color = Color(0xFF756681),
                    fontFamily = FontFamily.SansSerif
                )
            }
        }

        items(
            items = uiState.posts,
            key = { it.id }
        ) { post ->

            PostCard(
                post = post,
                onLikeToggled = {
                    // TODO: Handle like
                },
                onRevealRequested = onRevealRequested
            )
        }
    }
}

