package com.example.ivory.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ivory.ui.theme.screen.main.addPost.AddPostScreen
import com.example.ivory.ui.theme.screen.main.home.HomeScreen
import com.example.ivory.ui.theme.screen.main.home.ContentWarningScreen
import com.example.ivory.ui.theme.screen.main.home.RevealedContentScreen
import com.example.ivory.ui.theme.screen.main.profile.ProfileScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onRevealRequested = { postId ->
                    navController.navigate(Screen.ContentWarning.route(postId))
                }
            )
        }
        composable(Screen.AddPost.route) { AddPostScreen() }
        composable(Screen.Profile.route) { ProfileScreen(onLogout = onLogout) }
        composable(Screen.ContentWarning.route) { entry ->
            val postId = entry.arguments?.getString("postId") ?: return@composable
            ContentWarningScreen(
                postId = postId,
                onShowContent = { navController.navigate(Screen.RevealedContent.route(it)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.RevealedContent.route) { entry ->
            val postId = entry.arguments?.getString("postId") ?: return@composable
            RevealedContentScreen(
                postId = postId,
                onBack = { navController.popBackStack(Screen.Home.route, inclusive = false) }
            )
        }
    }
}
