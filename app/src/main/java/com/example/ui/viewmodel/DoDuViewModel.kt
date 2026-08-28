package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CommentEntity
import com.example.data.model.UserEntity
import com.example.data.model.VideoEntity
import com.example.data.repository.DoDuRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavDestination {
    FEED,
    STUDIO,
    SOFTWARE,
    MUSIC,
    PROFILE
}

enum class ProfileTab(val title: String) {
    UPLOADS("Uploads"),
    CLIPS("Clips"),
    LIKED("Liked")
}

enum class VideoFilter(val displayName: String, val description: String) {
    NORMAL("Normal", "Raw gaming feed at 1080p 60fps"),
    VIBRANT("Vibrant", "Punched saturation for esports color pop"),
    NEON("Cyber Glow", "Edge highlights and neon contrast"),
    RETRO("Arcade CRT", "Retro scanline textures and analog warmth"),
    CINEMATIC("Cinematic", "Wide dynamic range and mood shadows"),
    MONOCHROME("Noir Clutch", "Dramatic black & white contrast")
}

data class SoftwareRelease(
    val version: String,
    val date: String,
    val downloadSize: String,
    val isLatest: Boolean,
    val description: String,
    val highlights: List<String>,
    val downloadUrl: String
)

data class DamanMusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val genre: String,
    val bpm: Int
)

class DoDuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DoDuRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = DoDuRepository(db.userDao(), db.videoDao(), db.commentDao())
        viewModelScope.launch {
            repository.initializeSeedDataIfEmpty()
        }
    }

    // Navigation State
    private val _currentNav = MutableStateFlow(NavDestination.FEED)
    val currentNav: StateFlow<NavDestination> = _currentNav.asStateFlow()

    fun setNav(destination: NavDestination) {
        _currentNav.value = destination
        if (destination == NavDestination.PROFILE) {
            _selectedUserId.value = null
        }
    }

    // Current logged-in user
    val currentUser: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Currently selected / viewed user profile
    private val _selectedUserId = MutableStateFlow<String?>(null)
    val selectedUserId: StateFlow<String?> = _selectedUserId.asStateFlow()

    val profileUser: StateFlow<UserEntity?> = combine(currentUser, _selectedUserId) { current, selectedId ->
        if (selectedId == null) current else null
    }.flatMapLatest { directUser ->
        if (directUser != null) {
            flowOf(directUser)
        } else {
            val target = _selectedUserId.value
            if (target != null) repository.getUserById(target) else flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All registered users & gaming creators
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All videos on feed
    val allFeedVideos: StateFlow<List<VideoEntity>> = repository.allFeedVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Liked videos by current user
    val likedVideos: StateFlow<List<VideoEntity>> = repository.likedVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Profile videos
    val profileVideos: StateFlow<List<VideoEntity>> = profileUser.flatMapLatest { user ->
        if (user != null) repository.getVideosByCreator(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profileClips: StateFlow<List<VideoEntity>> = profileVideos.flatMapLatest { list ->
        flowOf(list.filter { it.isClip })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Profile Tab
    private val _selectedProfileTab = MutableStateFlow(ProfileTab.UPLOADS)
    val selectedProfileTab: StateFlow<ProfileTab> = _selectedProfileTab.asStateFlow()

    fun setProfileTab(tab: ProfileTab) {
        _selectedProfileTab.value = tab
    }

    fun viewUserProfile(userId: String) {
        _selectedUserId.value = userId
        _selectedProfileTab.value = ProfileTab.UPLOADS
        _currentNav.value = NavDestination.PROFILE
    }

    // Video Player
    private val _playingVideo = MutableStateFlow<VideoEntity?>(null)
    val playingVideo: StateFlow<VideoEntity?> = _playingVideo.asStateFlow()

    // Comments on the currently playing video
    val playingVideoComments: StateFlow<List<CommentEntity>> = _playingVideo.flatMapLatest { video ->
        if (video != null) repository.getCommentsForVideo(video.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun openVideoPlayer(video: VideoEntity) {
        _playingVideo.value = video
        viewModelScope.launch {
            repository.recordVideoView(video.id)
        }
    }

    fun closeVideoPlayer() {
        _playingVideo.value = null
    }

    fun postComment(
        videoId: String,
        content: String,
        parentCommentId: String? = null,
        replyToUsername: String? = null
    ) {
        val user = currentUser.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.addComment(
                videoId = videoId,
                user = user,
                content = content,
                parentCommentId = parentCommentId,
                replyToUsername = replyToUsername
            )
        }
    }

    fun toggleCommentLike(commentId: String, currentlyLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleCommentLike(commentId, currentlyLiked)
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
        }
    }

    fun toggleVideoLike(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleVideoLike(video.id, video.isLiked)
            if (_playingVideo.value?.id == video.id) {
                _playingVideo.value = _playingVideo.value?.copy(
                    isLiked = !video.isLiked,
                    likesCount = video.likesCount + (if (!video.isLiked) 1 else -1)
                )
            }
        }
    }

    fun deleteVideo(videoId: String) {
        viewModelScope.launch {
            repository.deleteVideo(videoId)
            if (_playingVideo.value?.id == videoId) {
                _playingVideo.value = null
            }
        }
    }

    // Profile Dialogs & Management
    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog: StateFlow<Boolean> = _showEditProfileDialog.asStateFlow()

    fun openEditProfile() {
        _showEditProfileDialog.value = true
    }

    fun closeEditProfile() {
        _showEditProfileDialog.value = false
    }

    fun saveProfile(
        username: String,
        displayName: String,
        bio: String,
        avatarDrawable: String?,
        favoriteGame: String,
        gamingRank: String
    ) {
        val targetUser = profileUser.value ?: currentUser.value ?: return
        viewModelScope.launch {
            repository.updateProfile(
                userId = targetUser.id,
                newUsername = username,
                newDisplayName = displayName,
                newBio = bio,
                newAvatarDrawable = avatarDrawable,
                favoriteGame = favoriteGame,
                gamingRank = gamingRank
            )
            _showEditProfileDialog.value = false
        }
    }

    fun switchUser(userId: String) {
        viewModelScope.launch {
            repository.switchActiveUser(userId)
            _selectedUserId.value = null
        }
    }

    // Upload Video Dialog
    private val _showUploadDialog = MutableStateFlow(false)
    val showUploadDialog: StateFlow<Boolean> = _showUploadDialog.asStateFlow()

    fun openUploadDialog() {
        _showUploadDialog.value = true
    }

    fun closeUploadDialog() {
        _showUploadDialog.value = false
    }

    fun uploadNewVideo(
        title: String,
        description: String,
        gameTitle: String,
        thumbnailName: String,
        duration: String,
        isClip: Boolean
    ) {
        val creator = currentUser.value ?: return
        viewModelScope.launch {
            repository.uploadVideo(
                creatorId = creator.id,
                title = title,
                description = description,
                gameTitle = gameTitle,
                thumbnailDrawableName = thumbnailName,
                duration = duration,
                isClip = isClip
            )
            _showUploadDialog.value = false
            _currentNav.value = NavDestination.PROFILE
        }
    }

    // ==========================================
    // Video Making and Editing Studio State
    // ==========================================
    private val _studioTitle = MutableStateFlow("Epic Ace Clutch in Cyberzone")
    val studioTitle: StateFlow<String> = _studioTitle.asStateFlow()

    private val _studioGame = MutableStateFlow("Apex Cyberzone")
    val studioGame: StateFlow<String> = _studioGame.asStateFlow()

    private val _studioThumbnail = MutableStateFlow("thumb_gameplay_1")
    val studioThumbnail: StateFlow<String> = _studioThumbnail.asStateFlow()

    private val _studioFilter = MutableStateFlow(VideoFilter.VIBRANT)
    val studioFilter: StateFlow<VideoFilter> = _studioFilter.asStateFlow()

    private val _studioTrimStartSec = MutableStateFlow(0f)
    val studioTrimStartSec: StateFlow<Float> = _studioTrimStartSec.asStateFlow()

    private val _studioTrimEndSec = MutableStateFlow(24f)
    val studioTrimEndSec: StateFlow<Float> = _studioTrimEndSec.asStateFlow()

    private val _studioSticker = MutableStateFlow("🔥 PENTAKILL")
    val studioSticker: StateFlow<String> = _studioSticker.asStateFlow()

    private val _studioSpeed = MutableStateFlow(1.0f)
    val studioSpeed: StateFlow<Float> = _studioSpeed.asStateFlow()

    private val _studioSelectedMusic = MutableStateFlow("Overdrive Synth Pulse")
    val studioSelectedMusic: StateFlow<String> = _studioSelectedMusic.asStateFlow()

    private val _isStudioExporting = MutableStateFlow(false)
    val isStudioExporting: StateFlow<Boolean> = _isStudioExporting.asStateFlow()

    private val _studioExportSuccess = MutableStateFlow(false)
    val studioExportSuccess: StateFlow<Boolean> = _studioExportSuccess.asStateFlow()

    fun setStudioTitle(title: String) { _studioTitle.value = title }
    fun setStudioGame(game: String) { _studioGame.value = game }
    fun setStudioThumbnail(thumbnail: String) { _studioThumbnail.value = thumbnail }
    fun setStudioFilter(filter: VideoFilter) { _studioFilter.value = filter }
    fun setStudioTrim(start: Float, end: Float) {
        _studioTrimStartSec.value = start
        _studioTrimEndSec.value = end
    }
    fun setStudioSticker(sticker: String) { _studioSticker.value = sticker }
    fun setStudioSpeed(speed: Float) { _studioSpeed.value = speed }
    fun setStudioMusic(track: String) { _studioSelectedMusic.value = track }

    fun exportAndPublishStudioVideo() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            _isStudioExporting.value = true
            _studioExportSuccess.value = false
            delay(1800) // Simulate fast hardware GPU rendering & encoding

            val durationSeconds = (_studioTrimEndSec.value - _studioTrimStartSec.value).toInt().coerceAtLeast(5)
            val durationFormatted = String.format("0:%02d", durationSeconds)

            repository.uploadVideo(
                creatorId = user.id,
                title = _studioTitle.value.ifBlank { "Gaming Highlight" },
                description = "Edited with Do+Du Studio • Filter: ${_studioFilter.value.displayName} • Audio: ${_studioSelectedMusic.value} • ${_studioSticker.value}",
                gameTitle = _studioGame.value,
                thumbnailDrawableName = _studioThumbnail.value,
                duration = durationFormatted,
                isClip = durationSeconds <= 60
            )

            _isStudioExporting.value = false
            _studioExportSuccess.value = true
            delay(1200)
            _studioExportSuccess.value = false
            _currentNav.value = NavDestination.PROFILE
        }
    }

    // ==========================================
    // Software & Updates State
    // ==========================================
    private val _installedSoftwareVersion = MutableStateFlow("v2.4.0 (Build 420)")
    val installedSoftwareVersion: StateFlow<String> = _installedSoftwareVersion.asStateFlow()

    private val _isCheckingForUpdates = MutableStateFlow(false)
    val isCheckingForUpdates: StateFlow<Boolean> = _isCheckingForUpdates.asStateFlow()

    private val _updateCheckResult = MutableStateFlow<String?>(null)
    val updateCheckResult: StateFlow<String?> = _updateCheckResult.asStateFlow()

    val softwareReleases = listOf(
        SoftwareRelease(
            version = "v2.5.0-Release",
            date = "May 2026",
            downloadSize = "48.2 MB",
            isLatest = true,
            description = "Major update featuring the Geometric Balance UI redesign, hardware-accelerated 120fps video timeline trimming, and integrated Daman Gaming soundtrack hub.",
            highlights = listOf(
                "Geometric Balance M3 design system with unified typography & layouts",
                "New Video Making & Editing Studio with real-time color grading filters",
                "Creator profile badges and custom gaming rank identifiers",
                "Integrated software update repository and direct APK package installer",
                "Optimized Room DB persistence with automatic schema migration"
            ),
            downloadUrl = "https://dodu.stream/downloads/dodu-v2.5.0-release.apk"
        ),
        SoftwareRelease(
            version = "v2.4.0-Stable",
            date = "March 2026",
            downloadSize = "43.7 MB",
            isLatest = false,
            description = "Performance patch improving 4K 60fps video buffering latency, chat comment moderation filters, and adaptive screen rails for tablet foldables.",
            highlights = listOf(
                "AVN Rail navigation for tablet and foldable displays",
                "Interactive in-player comments and likes sync",
                "Creator showcase carousels and game category filter chips"
            ),
            downloadUrl = "https://dodu.stream/downloads/dodu-v2.4.0-stable.apk"
        ),
        SoftwareRelease(
            version = "v2.3.1-LTS",
            date = "January 2026",
            downloadSize = "39.1 MB",
            isLatest = false,
            description = "Long-term support release with rock-solid video playback stability, memory footprint optimization, and low-latency audio sync.",
            highlights = listOf(
                "Offline video cache reduction",
                "Enhanced profile customization with instant avatar pickers"
            ),
            downloadUrl = "https://dodu.stream/downloads/dodu-v2.3.1-lts.apk"
        )
    )

    fun checkForSoftwareUpdates() {
        viewModelScope.launch {
            _isCheckingForUpdates.value = true
            _updateCheckResult.value = null
            delay(1500) // Simulate checking online update website
            _isCheckingForUpdates.value = false
            _updateCheckResult.value = "You are on the latest Do+Du release v2.5.0! All gaming streaming engines are up to date."
        }
    }

    // ==========================================
    // Gaming Music & Audio Hub (Daman Audio)
    // ==========================================
    val damanMusicTracks = listOf(
        DamanMusicTrack("t1", "Neon Drift Overdrive", "CyberByte", "2:45", "Synthwave", 128),
        DamanMusicTrack("t2", "Final Boss Requiem", "Daman Sound Lab", "3:18", "Orchestral Trap", 140),
        DamanMusicTrack("t3", "Pulse Velocity", "Aether Wave", "2:15", "Drum & Bass", 174),
        DamanMusicTrack("t4", "Apex Victory Theme", "RetroGlitch", "1:55", "Chiptune Electro", 132),
        DamanMusicTrack("t5", "Shadow Step Sneak", "Nightshade", "2:30", "Dark Ambient", 110),
        DamanMusicTrack("t6", "Laser Tag Championship", "Daman Sound Lab", "3:02", "Hyperpop EDM", 150)
    )

    private val _currentPlayingTrack = MutableStateFlow<DamanMusicTrack?>(null)
    val currentPlayingTrack: StateFlow<DamanMusicTrack?> = _currentPlayingTrack.asStateFlow()

    private val _isPlayingDamanAudio = MutableStateFlow(false)
    val isPlayingDamanAudio: StateFlow<Boolean> = _isPlayingDamanAudio.asStateFlow()

    fun togglePlayTrack(track: DamanMusicTrack) {
        if (_currentPlayingTrack.value?.id == track.id && _isPlayingDamanAudio.value) {
            _isPlayingDamanAudio.value = false
        } else {
            _currentPlayingTrack.value = track
            _isPlayingDamanAudio.value = true
        }
    }

    fun stopTrack() {
        _isPlayingDamanAudio.value = false
    }
}
