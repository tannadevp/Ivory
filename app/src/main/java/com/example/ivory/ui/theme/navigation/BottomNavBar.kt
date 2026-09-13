package com.example.ivory.ui.theme.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.AddPost,
        Screen.Profile
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFFF3EEFF), // 🌸 Light lavender
        tonalElevation = 4.dp
    ) {

        items.forEach { screen ->

            NavigationBarItem(
                selected = currentRoute == screen.route,

                onClick = {
                    navController.navigate(screen.route) {

                        popUpTo(
                            navController.graph.findStartDestination().id
                        ) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label
                    )
                },

                label = {
                    Text(
                        text = screen.label,
                        fontWeight = FontWeight.Medium
                    )
                },

                colors = NavigationBarItemDefaults.colors(

                    selectedIconColor = Color(0xFF6C4AB6),
                    selectedTextColor = Color(0xFF6C4AB6),

                    unselectedIconColor = Color(0xFF8B7A9E),
                    unselectedTextColor = Color(0xFF8B7A9E),

                    indicatorColor = Color(0xFFE4D7FA)
                )
            )
        }
    }
}

