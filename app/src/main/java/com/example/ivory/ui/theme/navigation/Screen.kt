package com.example.ivory.ui.theme.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object AddPost : Screen("add_post", "Post", Icons.Filled.AddCircle)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object ContentWarning : Screen("content_warning/{postId}", "Content warning", Icons.Filled.Warning) {
        fun route(postId: String) = "content_warning/$postId"
    }
    object RevealedContent : Screen("revealed_content/{postId}", "Revealed content", Icons.Filled.Warning) {
        fun route(postId: String) = "revealed_content/$postId"
    }
}
