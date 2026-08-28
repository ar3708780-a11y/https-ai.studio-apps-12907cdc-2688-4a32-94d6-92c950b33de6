package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoOnPrimaryContainer

fun resolveDrawableRes(name: String?): Int? = when (name) {
    "img_avatar_gamer" -> R.drawable.img_avatar_gamer
    "img_creator_avatar" -> R.drawable.img_creator_avatar
    "img_gamer_avatar2" -> R.drawable.img_gamer_avatar2
    "img_dodu_icon" -> R.drawable.img_dodu_icon
    "img_thumb_fps" -> R.drawable.img_thumb_fps
    "img_thumb_rpg" -> R.drawable.img_thumb_rpg
    "img_thumb_cyberpunk" -> R.drawable.img_thumb_cyberpunk
    "img_thumb_music" -> R.drawable.img_thumb_music
    "img_thumb_nature" -> R.drawable.img_thumb_nature
    "img_thumb_racing" -> R.drawable.img_thumb_racing
    else -> null
}

@Composable
fun UserAvatar(
    avatarDrawableName: String?,
    displayName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showOnlineStatus: Boolean = false,
    borderColor: Color = MaterialTheme.colorScheme.primary
) {
    val drawableRes = resolveDrawableRes(avatarDrawableName)
    val initials = displayName.trim().let { name ->
        if (name.isEmpty()) "D"
        else {
            val parts = name.split(" ").filter { it.isNotBlank() }
            if (parts.size >= 2) "${parts[0].first()}${parts[1].first()}".uppercase()
            else name.take(2).uppercase()
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .testTag("user_avatar")
    ) {
        if (drawableRes != null) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = "Avatar of $displayName",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(
                            width = if (size > 64.dp) 2.5.dp else 1.5.dp,
                            brush = Brush.linearGradient(
                                listOf(
                                    borderColor,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        ),
                        CircleShape
                    )
            )
        } else {
            // Optional avatar fallback: geometric initials avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                GeoPrimary,
                                Color(0xFF9A82DB)
                            )
                        )
                    )
                    .border(
                        BorderStroke(
                            width = if (size > 64.dp) 2.dp else 1.dp,
                            color = GeoPrimaryContainer
                        ),
                        CircleShape
                    )
            ) {
                val fontSize = (size.value * 0.38f).coerceAtLeast(10f).sp
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            }
        }

        if (showOnlineStatus) {
            val statusDotSize = (size * 0.28f).coerceAtLeast(10.dp)
            Box(
                modifier = Modifier
                    .size(statusDotSize)
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E6B3F)) // Active online indicator
                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}
