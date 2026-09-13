package com.example.ivory.ui.theme.screen.main.addPost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ivory.data.remote.dto.ModerationResponseDto

@Composable
fun AnalysisResultCard(result: ModerationResponseDto) {
    val statusColor = when {
        result.overallToxicity > 0.7f -> Color(0xFFFF4D6D)
        result.overallToxicity > 0.4f -> Color(0xFFFFC107)
        else -> Color(0xFF4CAF50)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Toxicity: ${(result.overallToxicity * 100).toInt()}%",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(result.message, color = Color.LightGray, fontSize = 13.sp)

            Spacer(Modifier.height(8.dp))
            Text(
                "Age rating: ${result.ageRating} (${result.ageRatingLevel})",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            if (result.anyFlagged || result.isSensitive) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${result.warningTitle}: ${result.warningReason}",
                        color = Color(0xFFFFC107),
                        fontSize = 13.sp
                    )
                }
            } else {
                Spacer(Modifier.height(8.dp))
                Text("✓ Looks good to post", color = Color(0xFF4CAF50), fontSize = 13.sp)
            }
        }
    }
}
