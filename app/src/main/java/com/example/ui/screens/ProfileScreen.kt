package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.data.model.VideoEntity
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.UploadVideoDialog
import com.example.ui.components.UserAvatar
import com.example.ui.components.VideoCard
import com.example.ui.components.formatCount
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RadiantCoral
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.VoidDark
import com.example.ui.viewmodel.ProfileTab

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    user: UserEntity?,
    isCurrentUser: Boolean,
    allCreators: List<UserEntity>,
    uploadedVideos: List<VideoEntity>,
    likedVideos: List<VideoEntity>,
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit,
    onPlayVideo: (VideoEntity) -> Unit,
    onToggleLike: (VideoEntity) -> Unit,
    onDeleteVideo: (VideoEntity) -> Unit,
    onSaveProfile: (
        username: String,
        displayName: String,
        bio: String,
        avatarDrawable: String?,
        favoriteGame: String,
        gamingRank: String
    ) -> Unit,
    onUploadVideo: (
        title: String,
        description: String,
        gameTitle: String,
        thumbnailDrawableName: String,
        duration: String,
        isClip: Boolean
    ) -> Unit,
    onSwitchUser: (String) -> Unit,
    onBackToMyProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (user == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(VoidDark),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading gamer profile...", color = Color.White)
        }
        return
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var showUserSwitcher by remember { mutableStateOf(false) }
    var isFollowing by remember { mutableStateOf(false) }

    val displayedVideos = when (selectedTab) {
        ProfileTab.UPLOADS -> uploadedVideos
        ProfileTab.CLIPS -> uploadedVideos.filter { it.isClip }
        ProfileTab.LIKED -> likedVideos
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VoidDark)
            .testTag("profile_screen")
    ) {
        // Banner & Avatar Area
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Cyber Gaming Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(user.bannerColorHex),
                                    CyberViolet,
                                    VoidDark
                                )
                            )
                        )
                ) {
                    // Top Navigation Actions on Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isCurrentUser) {
                            IconButton(
                                onClick = onBackToMyProfile,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .testTag("back_to_my_profile_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back to My Profile",
                                    tint = Color.White
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Do+Du Creator ID",
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Switch Profile Button
                        Box {
                            Button(
                                onClick = { showUserSwitcher = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black.copy(alpha = 0.65f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                modifier = Modifier.testTag("switch_user_menu_btn")
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isCurrentUser) "Switch Account" else "Accounts", fontSize = 12.sp)
                            }

                            DropdownMenu(
                                expanded = showUserSwitcher,
                                onDismissRequest = { showUserSwitcher = false },
                                modifier = Modifier
                                    .background(SurfaceDark)
                                    .border(1.dp, CyberViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            ) {
                                allCreators.forEach { creator ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                UserAvatar(
                                                    avatarDrawableName = creator.avatarDrawableName,
                                                    displayName = creator.displayName,
                                                    size = 28.dp
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(creator.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("@${creator.username}", color = NeonCyan, fontSize = 11.sp)
                                                }
                                            }
                                        },
                                        onClick = {
                                            showUserSwitcher = false
                                            onSwitchUser(creator.id)
                                        },
                                        modifier = Modifier.testTag("switch_to_user_${creator.username}")
                                    )
                                }
                            }
                        }
                    }
                }

                // Avatar positioned overlapping the banner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp)
                ) {
                    UserAvatar(
                        avatarDrawableName = user.avatarDrawableName,
                        displayName = user.displayName,
                        size = 88.dp,
                        showOnlineStatus = true,
                        borderColor = NeonCyan
                    )
                }
            }
        }

        // Profile Identity Details
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Display Name + Verified Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Creator",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Unique Username
                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    modifier = Modifier.testTag("profile_unique_username")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Gaming Tags & Rank Badges
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Favorite Game Chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberViolet.copy(alpha = 0.25f))
                            .border(1.dp, CyberViolet.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Gamepad, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(user.favoriteGame, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Gaming Rank Chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RadiantCoral.copy(alpha = 0.2f))
                            .border(1.dp, RadiantCoral.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = RadiantCoral, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(user.gamingRank, color = RadiantCoral, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Short Biography
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "BIOGRAPHY",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user.bio.ifBlank { "No biography added yet. Tap Edit Profile to customize." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp,
                            modifier = Modifier.testTag("profile_bio_text")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Statistics Row: Uploads, Followers, Following, Likes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceDark)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatItem(title = "Videos", count = formatCount(uploadedVideos.size.toLong()))
                    ProfileStatItem(title = "Followers", count = formatCount(user.followersCount.toLong()))
                    ProfileStatItem(title = "Following", count = formatCount(user.followingCount.toLong()))
                    ProfileStatItem(title = "Likes", count = formatCount(user.totalLikesCount.toLong()))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Profile Action Buttons
                if (isCurrentUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showEditDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceElevated,
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("edit_profile_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showUploadDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RadiantCoral,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_share_clip_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Game Clip", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { isFollowing = !isFollowing },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowing) SurfaceElevated else NeonCyan,
                                contentColor = if (isFollowing) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("follow_creator_button")
                        ) {
                            Text(if (isFollowing) "Following" else "Follow Creator", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onBackToMyProfile,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberViolet,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("My Profile", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section to Display Uploaded Videos (Tabs: Uploads, Clips, Liked)
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = Color.Transparent,
                    contentColor = NeonCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = NeonCyan,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    ProfileTab.entries.forEach { tab ->
                        val isTabSelected = selectedTab == tab
                        Tab(
                            selected = isTabSelected,
                            onClick = { onTabSelected(tab) },
                            text = {
                                Text(
                                    text = when (tab) {
                                        ProfileTab.UPLOADS -> "Uploads (${uploadedVideos.size})"
                                        ProfileTab.CLIPS -> "Clips (${uploadedVideos.count { it.isClip }})"
                                        ProfileTab.LIKED -> "Liked (${likedVideos.size})"
                                    },
                                    fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isTabSelected) NeonCyan else Color.White.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Uploaded Videos Content Section
        if (displayedVideos.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                        .testTag("empty_videos_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = CyberViolet,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = when (selectedTab) {
                                ProfileTab.UPLOADS -> "No gaming videos uploaded yet"
                                ProfileTab.CLIPS -> "No short clips shared yet"
                                ProfileTab.LIKED -> "No liked videos yet"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Record an epic gaming clutch and share it with the Do+Du community!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isCurrentUser && selectedTab != ProfileTab.LIKED) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showUploadDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RadiantCoral,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("empty_state_upload_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload First Video")
                            }
                        }
                    }
                }
            }
        } else {
            items(displayedVideos, key = { it.id }) { video ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    VideoCard(
                        video = video,
                        creator = user,
                        onPlay = onPlayVideo,
                        onCreatorClick = { /* Already on this profile */ },
                        onToggleLike = onToggleLike,
                        onDelete = if (isCurrentUser && selectedTab != ProfileTab.LIKED) {
                            { onDeleteVideo(video) }
                        } else null
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Dialogs
    if (showEditDialog) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditDialog = false },
            onSave = { username, displayName, bio, avatar, game, rank ->
                showEditDialog = false
                onSaveProfile(username, displayName, bio, avatar, game, rank)
            }
        )
    }

    if (showUploadDialog) {
        UploadVideoDialog(
            defaultGame = user.favoriteGame,
            onDismiss = { showUploadDialog = false },
            onUpload = { title, desc, game, thumb, dur, isClip ->
                showUploadDialog = false
                onUploadVideo(title, desc, game, thumb, dur, isClip)
            }
        )
    }
}

@Composable
fun ProfileStatItem(title: String, count: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
