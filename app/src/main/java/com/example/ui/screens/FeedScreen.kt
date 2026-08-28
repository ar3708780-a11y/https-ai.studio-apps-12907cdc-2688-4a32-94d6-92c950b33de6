package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.data.model.VideoEntity
import com.example.ui.components.UserAvatar
import com.example.ui.components.VideoCard
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RadiantCoral
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.VoidDark

@Composable
fun FeedScreen(
    videos: List<VideoEntity>,
    creators: List<UserEntity>,
    currentUser: UserEntity?,
    onPlayVideo: (VideoEntity) -> Unit,
    onCreatorClick: (String) -> Unit,
    onToggleLike: (VideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf("All") }

    val gameTags = listOf("All", "Apex Cyberzone", "Elden Horizon", "Neon Velocity GT", "Cyberpunk Arena")

    val creatorMap = remember(creators) {
        creators.associateBy { it.id }
    }

    val filteredVideos = videos.filter { video ->
        val matchesGame = selectedGame == "All" || video.gameTitle.equals(selectedGame, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                video.title.contains(searchQuery, ignoreCase = true) ||
                video.gameTitle.contains(searchQuery, ignoreCase = true) ||
                video.description.contains(searchQuery, ignoreCase = true)
        matchesGame && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VoidDark)
            .testTag("feed_screen_root"),
        contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
    ) {
        // App Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    Brush.linearGradient(listOf(RadiantCoral, CyberViolet)),
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Do+",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Do+Du Gaming",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Online Gaming Video Sharing",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (currentUser != null) {
                        Box(
                            modifier = Modifier
                                .clickable { onCreatorClick(currentUser.id) }
                                .testTag("feed_my_profile_avatar")
                        ) {
                            UserAvatar(
                                avatarDrawableName = currentUser.avatarDrawableName,
                                displayName = currentUser.displayName,
                                size = 38.dp,
                                showOnlineStatus = true,
                                borderColor = NeonCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search game clips, streamers, clutch plays...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feed_search_input")
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Active Creators Stories / Highlights
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "FEATURED STREAMERS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = NeonCyan,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(creators, key = { it.id }) { creator ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onCreatorClick(creator.id) }
                                .testTag("creator_story_${creator.id}")
                        ) {
                            UserAvatar(
                                avatarDrawableName = creator.avatarDrawableName,
                                displayName = creator.displayName,
                                size = 56.dp,
                                borderColor = NeonCyan,
                                showOnlineStatus = true
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = creator.displayName.split(" ").firstOrNull() ?: creator.username,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = "@${creator.username}",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.5f),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Game Category Filters
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(gameTags) { tag ->
                    val isSelected = selectedGame == tag
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGame = tag },
                        label = {
                            Text(
                                text = tag,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberViolet,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceDark,
                            labelColor = Color.White.copy(alpha = 0.7f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) NeonCyan else Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier.testTag("tag_chip_$tag")
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Videos Feed List
        if (filteredVideos.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No gaming clips found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching for a different game or keyword.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(filteredVideos, key = { it.id }) { video ->
                val creator = creatorMap[video.creatorId]
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    VideoCard(
                        video = video,
                        creator = creator,
                        onPlay = onPlayVideo,
                        onCreatorClick = onCreatorClick,
                        onToggleLike = onToggleLike
                    )
                }
            }
        }
    }
}
