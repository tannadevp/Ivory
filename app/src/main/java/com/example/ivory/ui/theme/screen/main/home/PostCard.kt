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
fun PostCard(post: Post, onLikeToggled: (Boolean) -> Unit) {
    var liked by remember { mutableStateOf(false) } 
    val scale by animateFloatAsState(if (liked) 1.2f else 1f, label = "likeScale")
    
    // Track whether a flagged/sensitive post has been manually revealed by the user
    var revealed by remember { mutableStateOf(false) }
    val isFlagged = post.moderation.shouldBlur

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(Modifier.padding(14.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = post.userAvatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(8.dp))
                Text(post.username, color = Color.White, fontWeight = FontWeight.SemiBold)

                Spacer(Modifier.weight(1f))
                if (isFlagged) {
                    AssistChip(
                        onClick = { revealed = !revealed },
                        label = { Text(if (revealed) "Hide" else "Reveal", fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (revealed) Color(0xFF444444) else Color(0xFFFF4D4D),
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (isFlagged && !revealed) {
                Text(
                    "Sensitive content is hidden. Tap Reveal to view it.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 28.dp)
                )
            } else {
                Column {
                Text(post.content, color = Color(0xFFE0E0E0), fontSize = 15.sp, lineHeight = 20.sp)

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
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "ML reviewed",
                    color = Color(0xFF8F7CFF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Age rating: ${post.moderation.ageRating}",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    liked = !liked
                    onLikeToggled(liked)
                }) {
                    Icon(
                        imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (liked) Color(0xFFFF4D6D) else Color.Gray,
                        modifier = Modifier.scale(scale)
                    )
                }
                Text("${post.likeCount + if (liked) 1 else 0}", color = Color.Gray, fontSize = 13.sp)

                Spacer(Modifier.width(16.dp))
                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment", tint = Color.Gray, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(4.dp))
                Text("${post.commentCount}", color = Color.Gray, fontSize = 13.sp)

                Spacer(Modifier.weight(1f))
                Icon(Icons.Outlined.Share, contentDescription = "Share", tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
        }
    }
}
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PostCardPreview() {
    PostCard(
        post = Post(
            id = "abc",
            username = "neha",
            userAvatarUrl = null,
            content = "Exploring how AI and ML can make everyday applications smarter!",
            imageUrl = null,
            likeCount = 24,
            commentCount = 6,
            timestamp = System.currentTimeMillis(),
            moderation = com.example.ivory.domain.model.ModerationInfo(
                toxicityScore = 0.72f,
                ageRating = "PG",
                isSensitive = false,
                reason = null
            )
        ),
        onLikeToggled = {}
    )
}
