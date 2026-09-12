package com.example.ivory.ui.theme.screen.main.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ivory.domain.dummy.dummyPosts
import com.example.ivory.viewModels.main.HomeViewModel

import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(modifier = modifier) {
        items(
            items = uiState.posts,
            key = { it.id }
        ) { post ->

            PostCard(
                post = post,
                onLikeToggled = { /* TODO */ }
            )
        }
    }
}