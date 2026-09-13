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
import androidx.compose.material3.Surface
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

    val toxicity = result.overallToxicity

    val statusColor = when {
        toxicity >= 0.85 -> Color(0xFFFF4D6D)
        toxicity >= 0.60 -> Color(0xFFFFC107)
        else -> Color(0xFF4CAF50)
    }

    val statusText = when {
        toxicity >= 0.85 -> "Highly toxic"
        toxicity >= 0.60 -> "Potentially harmful"
        else -> "Looks safe"
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            // ───────────── Status ─────────────

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = statusText,
                    color = Color(0xFF35234F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = "${(toxicity * 100).toInt()}%",
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            // ───────────── Message ─────────────

            Text(
                text = result.rating.message,
                color = Color(0xFF756681),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            // ───────────── Warning ─────────────

            if (result.anyFlagged || result.rating.isSensitive) {

                Spacer(Modifier.height(14.dp))

                Surface(
                    color = Color(0xFFFFF4D6),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.WarningAmber,
                                contentDescription = null,
                                tint = Color(0xFFE09B00),
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(Modifier.width(7.dp))

                            Text(
                                text = result.rating.warningTitle,
                                color = Color(0xFF8A6200),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = result.rating.warningReason,
                            color = Color(0xFF806A35),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // ───────────── Suggestion ─────────────

            result.suggestion?.let { suggestion ->

                Spacer(Modifier.height(14.dp))

                Surface(
                    color = Color(0xFFF3EEFF),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text(
                            text = "💡 Suggested rewrite",
                            color = Color(0xFF6C4AB6),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = suggestion.suggestedRewrite,
                            color = Color(0xFF35234F),
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // ───────────── Age rating ─────────────

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Age rating: ${result.rating.ageRating}",
                color = Color(0xFF756681),
                fontSize = 12.sp
            )
        }
    }
}