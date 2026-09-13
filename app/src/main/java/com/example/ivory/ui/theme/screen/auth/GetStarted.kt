package com.example.ivory.ui.theme.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ivory.R

@Composable
fun GetStartedScreen(
    onCreateAccount: () -> Unit,
    onLogIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Surface(
            modifier = Modifier.size(88.dp),
            shape = CircleShape,
            color = Color(0xFFE9DFFF), // Light purple
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFFD2C1F0)
            )
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Ivory logo",
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(30.dp))

        Text(
            text = "A safer place\nto share.",
            color = Color(0xFF35234F), // Dark purple
            fontSize = 34.sp,
            lineHeight = 41.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Share what matters, with a moment to pause and reflect before your post goes live.",
            color = Color(0xFF756681), // Muted purple-gray
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif
        )

        Spacer(Modifier.height(42.dp))

        Button(
            onClick = onCreateAccount,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C4AB6), // Purple button
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Create an account",
                modifier = Modifier.padding(vertical = 8.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        Spacer(Modifier.height(10.dp))

        TextButton(
            onClick = onLogIn,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Already have an account? ",
                color = Color(0xFF756681),
                fontFamily = FontFamily.SansSerif
            )

            Text(
                "Log in",
                color = Color(0xFF6C4AB6),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
