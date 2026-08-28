package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.GeoNavBg
import com.example.ui.theme.GeoNavPill
import com.example.ui.theme.GeoNavPillText
import com.example.ui.viewmodel.NavDestination

data class NavItemSpec(
    val destination: NavDestination,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val NAV_ITEMS = listOf(
    NavItemSpec(
        destination = NavDestination.FEED,
        label = "Feed",
        selectedIcon = Icons.Filled.VideoLibrary,
        unselectedIcon = Icons.Outlined.VideoLibrary,
        testTag = "nav_feed"
    ),
    NavItemSpec(
        destination = NavDestination.STUDIO,
        label = "Studio",
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome,
        testTag = "nav_studio"
    ),
    NavItemSpec(
        destination = NavDestination.SOFTWARE,
        label = "Updates",
        selectedIcon = Icons.Filled.SystemUpdate,
        unselectedIcon = Icons.Outlined.SystemUpdate,
        testTag = "nav_software"
    ),
    NavItemSpec(
        destination = NavDestination.MUSIC,
        label = "Audio Hub",
        selectedIcon = Icons.Filled.MusicNote,
        unselectedIcon = Icons.Outlined.MusicNote,
        testTag = "nav_music"
    ),
    NavItemSpec(
        destination = NavDestination.PROFILE,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_profile"
    )
)

@Composable
fun AdaptiveNavScaffold(
    currentNav: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    currentUser: UserEntity?,
    onQuickUpload: () -> Unit,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        if (useNavRail) {
            // Adaptive Vertical Navigation (AVN) Rail for Tablet / Landscape mode
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                NavigationRail(
                    containerColor = GeoNavBg,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 18.dp, bottom = 12.dp)
                        ) {
                            // Do+Du Brand Logo Badge
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Do+",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Do+Du",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Quick Upload Floating Action Button on AVN Rail
                            FloatingActionButton(
                                onClick = onQuickUpload,
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("avn_quick_upload_fab")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Upload Game Clip",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .testTag("avn_navigation_rail")
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    NAV_ITEMS.forEach { item ->
                        val isSelected = currentNav == item.destination
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { onNavigate(item.destination) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = GeoNavPillText,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = GeoNavPillText,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = GeoNavPill
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Current User Shortcut at bottom of rail
                    if (currentUser != null) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 18.dp)
                                .clickable { onNavigate(NavDestination.PROFILE) }
                                .testTag("avn_rail_user_avatar"),
                            contentAlignment = Alignment.Center
                        ) {
                            UserAvatar(
                                avatarDrawableName = currentUser.avatarDrawableName,
                                displayName = currentUser.displayName,
                                size = 40.dp,
                                borderColor = if (currentNav == NavDestination.PROFILE) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                        }
                    }
                }

                // Main Content next to AVN Rail
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    content()
                }
            }
        } else {
            // Mobile Layout: Bottom Navigation Bar with Quick Upload FAB
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    NavigationBar(
                        containerColor = GeoNavBg,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = 3.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        NAV_ITEMS.forEach { item ->
                            val isSelected = currentNav == item.destination
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { onNavigate(item.destination) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 10.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = GeoNavPillText,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    selectedTextColor = GeoNavPillText,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = GeoNavPill
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (currentNav != NavDestination.PROFILE && currentNav != NavDestination.STUDIO) {
                        FloatingActionButton(
                            onClick = onQuickUpload,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape,
                            modifier = Modifier.testTag("mobile_quick_upload_fab")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Upload Game Clip"
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    content()
                }
            }
        }
    }
}
