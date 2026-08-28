package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AdaptiveNavScaffold
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.UploadVideoDialog
import com.example.ui.components.VideoPlayerModal
import com.example.ui.screens.DamanMusicScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SoftwareUpdatesScreen
import com.example.ui.screens.VideoStudioScreen
import com.example.ui.theme.DoDuTheme
import com.example.ui.viewmodel.DoDuViewModel
import com.example.ui.viewmodel.NavDestination

class MainActivity : ComponentActivity() {

    private val viewModel: DoDuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoDuTheme(darkTheme = false) {
                DoDuApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DoDuApp(
    viewModel: DoDuViewModel,
    modifier: Modifier = Modifier
) {
    val currentNav by viewModel.currentNav.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val profileUser by viewModel.profileUser.collectAsStateWithLifecycle()
    val selectedUserId by viewModel.selectedUserId.collectAsStateWithLifecycle()
    val profileVideos by viewModel.profileVideos.collectAsStateWithLifecycle()
    val likedVideos by viewModel.likedVideos.collectAsStateWithLifecycle()
    val allFeedVideos by viewModel.allFeedVideos.collectAsStateWithLifecycle()
    val selectedProfileTab by viewModel.selectedProfileTab.collectAsStateWithLifecycle()
    val playingVideo by viewModel.playingVideo.collectAsStateWithLifecycle()
    val playingVideoComments by viewModel.playingVideoComments.collectAsStateWithLifecycle()
    val showEditProfileDialog by viewModel.showEditProfileDialog.collectAsStateWithLifecycle()
    val showUploadDialog by viewModel.showUploadDialog.collectAsStateWithLifecycle()

    // Video Studio state
    val studioTitle by viewModel.studioTitle.collectAsStateWithLifecycle()
    val studioGame by viewModel.studioGame.collectAsStateWithLifecycle()
    val studioThumbnail by viewModel.studioThumbnail.collectAsStateWithLifecycle()
    val studioFilter by viewModel.studioFilter.collectAsStateWithLifecycle()
    val studioTrimStart by viewModel.studioTrimStartSec.collectAsStateWithLifecycle()
    val studioTrimEnd by viewModel.studioTrimEndSec.collectAsStateWithLifecycle()
    val studioSticker by viewModel.studioSticker.collectAsStateWithLifecycle()
    val studioSpeed by viewModel.studioSpeed.collectAsStateWithLifecycle()
    val studioMusic by viewModel.studioSelectedMusic.collectAsStateWithLifecycle()
    val isStudioExporting by viewModel.isStudioExporting.collectAsStateWithLifecycle()
    val studioExportSuccess by viewModel.studioExportSuccess.collectAsStateWithLifecycle()

    // Software updates state
    val installedVersion by viewModel.installedSoftwareVersion.collectAsStateWithLifecycle()
    val isCheckingUpdates by viewModel.isCheckingForUpdates.collectAsStateWithLifecycle()
    val checkResult by viewModel.updateCheckResult.collectAsStateWithLifecycle()
    val softwareReleases = viewModel.softwareReleases

    // Audio soundtrack state
    val currentAudioTrack by viewModel.currentPlayingTrack.collectAsStateWithLifecycle()
    val isPlayingAudio by viewModel.isPlayingDamanAudio.collectAsStateWithLifecycle()

    AdaptiveNavScaffold(
        currentNav = currentNav,
        onNavigate = { dest -> viewModel.setNav(dest) },
        currentUser = currentUser,
        onQuickUpload = { viewModel.openUploadDialog() }
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            when (currentNav) {
                NavDestination.FEED -> {
                    FeedScreen(
                        videos = allFeedVideos,
                        creators = allUsers,
                        currentUser = currentUser,
                        onPlayVideo = { video -> viewModel.openVideoPlayer(video) },
                        onCreatorClick = { creatorId -> viewModel.viewUserProfile(creatorId) },
                        onToggleLike = { video -> viewModel.toggleVideoLike(video) }
                    )
                }

                NavDestination.STUDIO -> {
                    VideoStudioScreen(
                        title = studioTitle,
                        game = studioGame,
                        thumbnail = studioThumbnail,
                        activeFilter = studioFilter,
                        trimStart = studioTrimStart,
                        trimEnd = studioTrimEnd,
                        sticker = studioSticker,
                        speed = studioSpeed,
                        musicTrack = studioMusic,
                        isExporting = isStudioExporting,
                        exportSuccess = studioExportSuccess,
                        onTitleChange = { viewModel.setStudioTitle(it) },
                        onGameChange = { viewModel.setStudioGame(it) },
                        onThumbnailChange = { viewModel.setStudioThumbnail(it) },
                        onFilterChange = { viewModel.setStudioFilter(it) },
                        onTrimChange = { start, end -> viewModel.setStudioTrim(start, end) },
                        onStickerChange = { viewModel.setStudioSticker(it) },
                        onSpeedChange = { viewModel.setStudioSpeed(it) },
                        onMusicChange = { viewModel.setStudioMusic(it) },
                        onExportAndPublish = { viewModel.exportAndPublishStudioVideo() }
                    )
                }

                NavDestination.SOFTWARE -> {
                    SoftwareUpdatesScreen(
                        installedVersion = installedVersion,
                        isChecking = isCheckingUpdates,
                        checkResult = checkResult,
                        releases = softwareReleases,
                        onCheckForUpdates = { viewModel.checkForSoftwareUpdates() }
                    )
                }

                NavDestination.MUSIC -> {
                    DamanMusicScreen(
                        tracks = viewModel.damanMusicTracks,
                        currentTrack = currentAudioTrack,
                        isPlaying = isPlayingAudio,
                        onTogglePlay = { track -> viewModel.togglePlayTrack(track) },
                        onUseInStudio = { track ->
                            viewModel.setStudioMusic(track.title)
                            viewModel.setNav(NavDestination.STUDIO)
                        }
                    )
                }

                NavDestination.PROFILE -> {
                    val isCurrent = selectedUserId == null || (currentUser != null && currentUser?.id == selectedUserId)
                    ProfileScreen(
                        user = profileUser ?: currentUser,
                        isCurrentUser = isCurrent,
                        allCreators = allUsers,
                        uploadedVideos = profileVideos,
                        likedVideos = likedVideos,
                        selectedTab = selectedProfileTab,
                        onTabSelected = { tab -> viewModel.setProfileTab(tab) },
                        onPlayVideo = { video -> viewModel.openVideoPlayer(video) },
                        onToggleLike = { video -> viewModel.toggleVideoLike(video) },
                        onDeleteVideo = { video -> viewModel.deleteVideo(video.id) },
                        onSaveProfile = { username, displayName, bio, avatar, favGame, rank ->
                            viewModel.saveProfile(username, displayName, bio, avatar, favGame, rank)
                        },
                        onUploadVideo = { title, desc, game, thumb, dur, isClip ->
                            viewModel.uploadNewVideo(title, desc, game, thumb, dur, isClip)
                        },
                        onSwitchUser = { userId ->
                            viewModel.switchUser(userId)
                        },
                        onBackToMyProfile = {
                            viewModel.setNav(NavDestination.PROFILE)
                        }
                    )
                }
            }
        }
    }

    // Modal Video Player
    playingVideo?.let { video ->
        val creator = allUsers.find { it.id == video.creatorId }
        VideoPlayerModal(
            video = video,
            creator = creator,
            currentUser = currentUser,
            comments = playingVideoComments,
            onDismiss = { viewModel.closeVideoPlayer() },
            onToggleLike = { viewModel.toggleVideoLike(video) },
            onCreatorClick = { creatorId ->
                viewModel.closeVideoPlayer()
                viewModel.viewUserProfile(creatorId)
            },
            onPostComment = { content, parentCommentId, replyToUsername ->
                viewModel.postComment(video.id, content, parentCommentId, replyToUsername)
            },
            onToggleCommentLike = { commentId, isLiked ->
                viewModel.toggleCommentLike(commentId, isLiked)
            },
            onDeleteComment = { commentId ->
                viewModel.deleteComment(commentId)
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog && currentUser != null) {
        EditProfileDialog(
            user = currentUser!!,
            onDismiss = { viewModel.closeEditProfile() },
            onSave = { username, displayName, bio, avatar, game, rank ->
                viewModel.saveProfile(username, displayName, bio, avatar, game, rank)
            }
        )
    }

    // Upload Video Dialog
    if (showUploadDialog && currentUser != null) {
        UploadVideoDialog(
            defaultGame = currentUser?.favoriteGame ?: "Apex Cyberzone",
            onDismiss = { viewModel.closeUploadDialog() },
            onUpload = { title, desc, game, thumb, dur, isClip ->
                viewModel.uploadNewVideo(title, desc, game, thumb, dur, isClip)
            }
        )
    }
}
