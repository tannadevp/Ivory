package com.example.ivory.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ivory.ui.theme.screen.main.addPost.AddPostScreen
import com.example.ivory.ui.theme.screen.main.home.HomeScreen
import com.example.ivory.ui.theme.screen.main.profile.ProfileScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.AddPost.route) { AddPostScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
    }
}
