package com.example.ivory.ui.theme.screen.main.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.ivory.domain.model.Post

@Composable
fun PostCard(
    post: Post,
    onLikeToggled: (Boolean) -> Unit,
    onRevealRequested: (String) -> Unit
) {
    var liked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (liked) 1.2f else 1f,
        label = "likeScale"
    )

    val isFlagged = post.moderation.shouldBlur

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE9DFFF) // 💜 Light purple card
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = post.userAvatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = post.username,
                    color = Color(0xFF35234F),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                Spacer(Modifier.weight(1f))

                if (isFlagged) {
                    AssistChip(
                        onClick = {
                            onRevealRequested(post.id)
                        },
                        label = {
                            Text(
                                "Reveal",
                                fontSize = 11.sp
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFF6C4AB6),
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            if (isFlagged) {

                Text(
                    text = "Sensitive content is hidden. Tap Reveal to view it.",
                    color = Color(0xFF756681),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 28.dp)
                )

            } else {

                Column {

                    Text(
                        text = post.content,
                        color = Color(0xFF453657),
                        fontSize = 15.sp,
                        lineHeight = 21.sp
                    )

                    post.imageUrl?.let {

                        Spacer(Modifier.height(10.dp))

                        AsyncImage(
                            model = it,
                            contentDescription = "Post image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 260.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "ML reviewed",
                    color = Color(0xFF6C4AB6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Age rating: ${post.moderation.ageRating}",
                    color = Color(0xFF756681),
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        liked = !liked
                        onLikeToggled(liked)
                    }
                ) {

                    Icon(
                        imageVector = if (liked)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,

                        contentDescription = "Like",

                        tint = if (liked)
                            Color(0xFF9B4DFF)
                        else
                            Color(0xFF756681),

                        modifier = Modifier.scale(scale)
                    )
                }

                Text(
                    text = "${post.likeCount + if (liked) 1 else 0}",
                    color = Color(0xFF756681),
                    fontSize = 13.sp
                )

                Spacer(Modifier.width(16.dp))

                Icon(
                    Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Comment",
                    tint = Color(0xFF756681),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = "${post.commentCount}",
                    color = Color(0xFF756681),
                    fontSize = 13.sp
                )

                Spacer(Modifier.weight(1f))

                Icon(
                    Icons.Outlined.Share,
                    contentDescription = "Share",
                    tint = Color(0xFF756681),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


