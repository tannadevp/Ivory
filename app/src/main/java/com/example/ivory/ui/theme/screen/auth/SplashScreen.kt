package com.example.ivory.ui.theme.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ivory.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onLoadingComplete: () -> Unit,
    loadingDurationMillis: Long = 1_500L
) {
    LaunchedEffect(Unit) {
        delay(loadingDurationMillis)
        onLoadingComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCF8)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(116.dp),
                shape = CircleShape,
                color = Color(0xFFFFF9F1),
                border = BorderStroke(1.dp, Color(0xFFE9DDCE))
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Ivory logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(116.dp)
                )
            }
            Spacer(Modifier.height(32.dp))

            Text(
                text = "A Safer Place to Share",
                color = Color(0xFF796C60),
                fontSize = 15.sp
            )
            Spacer(Modifier.height(36.dp))
            CircularProgressIndicator(
                color = Color(0xFF5A4636),
                trackColor = Color(0xFFEDE2D5),
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
