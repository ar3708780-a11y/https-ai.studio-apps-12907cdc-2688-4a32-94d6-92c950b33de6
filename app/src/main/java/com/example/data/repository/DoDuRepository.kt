package com.example.data.repository

import com.example.data.local.CommentDao
import com.example.data.local.UserDao
import com.example.data.local.VideoDao
import com.example.data.model.CommentEntity
import com.example.data.model.UserEntity
import com.example.data.model.VideoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class DoDuRepository(
    private val userDao: UserDao,
    private val videoDao: VideoDao,
    private val commentDao: CommentDao
) {
    val currentUser: Flow<UserEntity?> = userDao.getCurrentUser()
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allFeedVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()
    val likedVideos: Flow<List<VideoEntity>> = videoDao.getLikedVideos()

    fun getUserById(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)

    fun getVideosByCreator(creatorId: String): Flow<List<VideoEntity>> =
        videoDao.getVideosByCreator(creatorId)

    fun getClipsByCreator(creatorId: String): Flow<List<VideoEntity>> =
        videoDao.getClipsByCreator(creatorId)

    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>> =
        commentDao.getCommentsForVideo(videoId)

    suspend fun addComment(
        videoId: String,
        user: UserEntity,
        content: String,
        parentCommentId: String? = null,
        replyToUsername: String? = null
    ): CommentEntity {
        val newComment = CommentEntity(
            id = "c_" + UUID.randomUUID().toString().take(8),
            videoId = videoId,
            userId = user.id,
            authorName = user.displayName,
            authorUsername = user.username,
            authorAvatarDrawable = user.avatarDrawableName,
            authorRank = user.gamingRank,
            content = content.trim(),
            timestamp = System.currentTimeMillis(),
            parentCommentId = parentCommentId,
            replyToUsername = replyToUsername,
            likesCount = 0,
            isLiked = false
        )
        commentDao.insertComment(newComment)
        return newComment
    }

    suspend fun toggleCommentLike(commentId: String, currentlyLiked: Boolean) {
        val nextLiked = !currentlyLiked
        val delta = if (nextLiked) 1 else -1
        commentDao.toggleCommentLike(commentId, nextLiked, delta)
    }

    suspend fun deleteComment(commentId: String) {
        commentDao.deleteComment(commentId)
    }

    suspend fun updateProfile(
        userId: String,
        newUsername: String,
        newDisplayName: String,
        newBio: String,
        newAvatarDrawable: String?,
        customAvatarUri: String? = null,
        websiteUrl: String = "",
        favoriteGame: String = "Apex Cyberzone",
        gamingRank: String = "Apex Predator"
    ) {
        val existing = userDao.getUserById(userId).firstOrNull()
        if (existing != null) {
            val cleanUsername = newUsername.trim().removePrefix("@").ifBlank { existing.username }
            val updated = existing.copy(
                username = cleanUsername,
                displayName = newDisplayName.trim().ifBlank { existing.displayName },
                bio = newBio.trim(),
                avatarDrawableName = newAvatarDrawable,
                customAvatarUri = customAvatarUri ?: existing.customAvatarUri,
                websiteUrl = websiteUrl.trim().ifBlank { existing.websiteUrl },
                favoriteGame = favoriteGame.trim().ifBlank { existing.favoriteGame },
                gamingRank = gamingRank.trim().ifBlank { existing.gamingRank }
            )
            userDao.updateUser(updated)
        }
    }

    suspend fun clearCustomAvatar(userId: String) {
        val existing = userDao.getUserById(userId).firstOrNull()
        if (existing != null) {
            userDao.updateUser(existing.copy(customAvatarUri = null))
        }
    }

    suspend fun switchActiveUser(userId: String) {
        userDao.clearCurrentUserFlag()
        userDao.setCurrentUser(userId)
    }

    suspend fun uploadVideo(
        creatorId: String,
        title: String,
        description: String,
        gameTitle: String,
        thumbnailDrawableName: String,
        duration: String,
        isClip: Boolean,
        musicTrack: String = "Daman Bass Wave #01",
        customThumbnailUri: String? = null,
        resolution: String = "4K 60FPS"
    ): VideoEntity {
        val newVideo = VideoEntity(
            id = "v_" + UUID.randomUUID().toString().take(8),
            creatorId = creatorId,
            title = title.trim(),
            description = description.trim(),
            gameTitle = gameTitle.trim(),
            thumbnailDrawableName = thumbnailDrawableName,
            customThumbnailUri = customThumbnailUri,
            musicTrack = musicTrack,
            duration = duration,
            isClip = isClip,
            viewsCount = 1,
            likesCount = 0,
            isLiked = false,
            timestamp = System.currentTimeMillis(),
            resolution = resolution
        )
        videoDao.insertVideo(newVideo)
        return newVideo
    }

    suspend fun toggleVideoLike(videoId: String, currentlyLiked: Boolean) {
        val nextLiked = !currentlyLiked
        val delta = if (nextLiked) 1 else -1
        videoDao.toggleLike(videoId, nextLiked, delta)
    }

    suspend fun recordVideoView(videoId: String) {
        videoDao.incrementViews(videoId)
    }

    suspend fun deleteVideo(videoId: String) {
        videoDao.deleteVideo(videoId)
    }

    suspend fun initializeSeedDataIfEmpty() {
        if (userDao.getUserCount() == 0) {
            val defaultUsers = listOf(
                UserEntity(
                    id = "u_primary",
                    username = "neon_valkyrie",
                    displayName = "Valkyrie Ray",
                    bio = "Competitive FPS predator & clip creator 🎮 Daily 1v5 clutch highlights & movement guides! Welcome to DoDu.",
                    avatarDrawableName = "img_avatar_gamer",
                    bannerColorHex = 0xFF6C5CE7,
                    favoriteGame = "Apex Cyberzone",
                    gamingRank = "Apex Predator #42",
                    websiteUrl = "https://daman.music.share/valkyrie",
                    followersCount = 24800,
                    followingCount = 135,
                    totalLikesCount = 154200,
                    isCurrentUser = true
                ),
                UserEntity(
                    id = "u_pixel",
                    username = "pixel_ninja",
                    displayName = "Kai Storm",
                    bio = "Mythic RPG Speedrunner ⚡ Beating Soulslike bosses at Level 1 with no damage. Daily DoDu streams & clips.",
                    avatarDrawableName = "img_creator_avatar",
                    bannerColorHex = 0xFF00B894,
                    favoriteGame = "Elden Horizon",
                    gamingRank = "World Record Holder",
                    websiteUrl = "https://daman.music.share/pixelbeats",
                    followersCount = 88400,
                    followingCount = 420,
                    totalLikesCount = 430100,
                    isCurrentUser = false
                ),
                UserEntity(
                    id = "u_glitch",
                    username = "glitch_reaper",
                    displayName = "Marcus Vance",
                    bio = "Sim-Racer & Neon Hypercar drifter 🏁 Clean lines, close tandems, and cinematic cockpit replays.",
                    avatarDrawableName = null, // Demonstrates optional avatar fallback with monogram
                    bannerColorHex = 0xFFFF7675,
                    favoriteGame = "Neon Velocity GT",
                    gamingRank = "Tier 1 Champion",
                    websiteUrl = "https://daman.music.share/driftvibes",
                    followersCount = 15600,
                    followingCount = 88,
                    totalLikesCount = 62000,
                    isCurrentUser = false
                )
            )
            userDao.insertUsers(defaultUsers)
        }

        if (videoDao.getVideoCount() == 0) {
            val defaultVideos = listOf(
                VideoEntity(
                    id = "v_101",
                    creatorId = "u_primary",
                    title = "INSANE 1v5 Defuse Clutch in Ranked Tournament Finals!",
                    description = "Last second clutch play with precision plasma sniper. Teammates went wild in the comms!",
                    gameTitle = "Apex Cyberzone",
                    thumbnailDrawableName = "img_thumb_fps",
                    duration = "01:24",
                    isClip = true,
                    viewsCount = 45200,
                    likesCount = 6810,
                    isLiked = true,
                    resolution = "4K 60FPS"
                ),
                VideoEntity(
                    id = "v_102",
                    creatorId = "u_primary",
                    title = "Complete S-Tier Movement & Recoil Mastery Guide",
                    description = "Comprehensive breakdown of slide-cancels, bunny hopping, and horizontal recoil smoothing.",
                    gameTitle = "Apex Cyberzone",
                    thumbnailDrawableName = "img_thumb_cyberpunk",
                    duration = "08:45",
                    isClip = false,
                    viewsCount = 112400,
                    likesCount = 15400,
                    isLiked = false,
                    resolution = "4K 60FPS"
                ),
                VideoEntity(
                    id = "v_103",
                    creatorId = "u_primary",
                    title = "Plasma Rifle Secret Instant-Reset Technique",
                    description = "Fast 30-second tech tip to eliminate weapon kick during high-speed vertical slides.",
                    gameTitle = "Apex Cyberzone",
                    thumbnailDrawableName = "img_thumb_fps",
                    duration = "00:38",
                    isClip = true,
                    viewsCount = 28900,
                    likesCount = 4120,
                    isLiked = false,
                    resolution = "1080p 120FPS"
                ),
                VideoEntity(
                    id = "v_104",
                    creatorId = "u_pixel",
                    title = "Flawless Dragon Boss Solo Raid (No Hit, Level 1)",
                    description = "14 hours of attempts condensed into a perfect parry symphony against the Thunder Wyrm.",
                    gameTitle = "Elden Horizon",
                    thumbnailDrawableName = "img_thumb_rpg",
                    duration = "14:20",
                    isClip = false,
                    viewsCount = 230500,
                    likesCount = 42300,
                    isLiked = true,
                    resolution = "4K HDR"
                ),
                VideoEntity(
                    id = "v_105",
                    creatorId = "u_glitch",
                    title = "Tokyo Midnight Highway Drift: 360 Entry 🚗💨",
                    description = "Full throttle tandem drift on the suspension bridge in pouring rain. Pure adrenaline.",
                    gameTitle = "Neon Velocity GT",
                    thumbnailDrawableName = "img_thumb_music",
                    duration = "00:52",
                    isClip = true,
                    viewsCount = 86400,
                    likesCount = 12400,
                    isLiked = false,
                    resolution = "4K 60FPS"
                )
            )
            videoDao.insertVideos(defaultVideos)
        }

        if (commentDao.getCommentCount() == 0) {
            val now = System.currentTimeMillis()
            val seedComments = listOf(
                // Comments on v_101 (Valkyrie's clutch video)
                CommentEntity(
                    id = "c_101_1",
                    videoId = "v_101",
                    userId = "u_pixel",
                    authorName = "Kai Storm",
                    authorUsername = "pixel_ninja",
                    authorAvatarDrawable = "img_creator_avatar",
                    authorRank = "World Record Holder",
                    content = "That 0:18 flick shot through the smoke was unreal! What DPI and sens are you running?",
                    timestamp = now - (60 * 60 * 1000L), // 1 hour ago
                    parentCommentId = null,
                    replyToUsername = null,
                    likesCount = 24,
                    isLiked = true
                ),
                CommentEntity(
                    id = "r_101_1_1",
                    videoId = "v_101",
                    userId = "u_primary",
                    authorName = "Valkyrie Ray",
                    authorUsername = "neon_valkyrie",
                    authorAvatarDrawable = "img_avatar_gamer",
                    authorRank = "Apex Predator #42",
                    content = "Thanks Kai! 800 DPI, 1.2 in-game sens with raw input enabled 🔥",
                    timestamp = now - (45 * 60 * 1000L), // 45m ago
                    parentCommentId = "c_101_1",
                    replyToUsername = "pixel_ninja",
                    likesCount = 15,
                    isLiked = false
                ),
                CommentEntity(
                    id = "r_101_1_2",
                    videoId = "v_101",
                    userId = "u_glitch",
                    authorName = "Marcus Vance",
                    authorUsername = "glitch_reaper",
                    authorAvatarDrawable = null,
                    authorRank = "Tier 1 Champion",
                    content = "Cleanest crosshair placement I've seen all season. The defuse at 0.1s left had my heart stopping.",
                    timestamp = now - (30 * 60 * 1000L), // 30m ago
                    parentCommentId = "c_101_1",
                    replyToUsername = "pixel_ninja",
                    likesCount = 8,
                    isLiked = false
                ),
                CommentEntity(
                    id = "c_101_2",
                    videoId = "v_101",
                    userId = "u_glitch",
                    authorName = "Marcus Vance",
                    authorUsername = "glitch_reaper",
                    authorAvatarDrawable = null,
                    authorRank = "Tier 1 Champion",
                    content = "The audio cue timing on that flank was textbook. Did you hear his footsteps over the airstrike?",
                    timestamp = now - (2 * 60 * 60 * 1000L), // 2h ago
                    parentCommentId = null,
                    replyToUsername = null,
                    likesCount = 19,
                    isLiked = false
                ),
                CommentEntity(
                    id = "r_101_2_1",
                    videoId = "v_101",
                    userId = "u_primary",
                    authorName = "Valkyrie Ray",
                    authorUsername = "neon_valkyrie",
                    authorAvatarDrawable = "img_avatar_gamer",
                    authorRank = "Apex Predator #42",
                    content = "Yes! Spatial audio equalizer mode turned up between 2-4kHz brings footsteps right out.",
                    timestamp = now - (90 * 60 * 1000L),
                    parentCommentId = "c_101_2",
                    replyToUsername = "glitch_reaper",
                    likesCount = 11,
                    isLiked = false
                ),
                CommentEntity(
                    id = "c_101_3",
                    videoId = "v_101",
                    userId = "u_pixel",
                    authorName = "Kai Storm",
                    authorUsername = "pixel_ninja",
                    authorAvatarDrawable = "img_creator_avatar",
                    authorRank = "World Record Holder",
                    content = "Tutorial on this slide-cancel entry when?? My lobby needs saving 😂",
                    timestamp = now - (3 * 60 * 60 * 1000L),
                    parentCommentId = null,
                    replyToUsername = null,
                    likesCount = 9,
                    isLiked = false
                ),

                // Comments on v_104 (Kai's Dragon boss raid)
                CommentEntity(
                    id = "c_104_1",
                    videoId = "v_104",
                    userId = "u_primary",
                    authorName = "Valkyrie Ray",
                    authorUsername = "neon_valkyrie",
                    authorAvatarDrawable = "img_avatar_gamer",
                    authorRank = "Apex Predator #42",
                    content = "14 hours well spent!! The dodge timing on the phase 2 lightning breath was millimeter perfect.",
                    timestamp = now - (4 * 60 * 60 * 1000L),
                    parentCommentId = null,
                    replyToUsername = null,
                    likesCount = 42,
                    isLiked = true
                ),
                CommentEntity(
                    id = "r_104_1_1",
                    videoId = "v_104",
                    userId = "u_pixel",
                    authorName = "Kai Storm",
                    authorUsername = "pixel_ninja",
                    authorAvatarDrawable = "img_creator_avatar",
                    authorRank = "World Record Holder",
                    content = "Appreciate it Valkyrie! Counted 128 parries total in that run. My thumb was shaking at the end!",
                    timestamp = now - (3 * 60 * 60 * 1000L),
                    parentCommentId = "c_104_1",
                    replyToUsername = "neon_valkyrie",
                    likesCount = 27,
                    isLiked = false
                ),

                // Comments on v_105 (Marcus Vance's Drift video)
                CommentEntity(
                    id = "c_105_1",
                    videoId = "v_105",
                    userId = "u_pixel",
                    authorName = "Kai Storm",
                    authorUsername = "pixel_ninja",
                    authorAvatarDrawable = "img_creator_avatar",
                    authorRank = "World Record Holder",
                    content = "That 360 reverse entry in the rain looked like CGI! Unreal steering wheel control 🏁",
                    timestamp = now - (5 * 60 * 60 * 1000L),
                    parentCommentId = null,
                    replyToUsername = null,
                    likesCount = 31,
                    isLiked = false
                ),
                CommentEntity(
                    id = "r_105_1_1",
                    videoId = "v_105",
                    userId = "u_glitch",
                    authorName = "Marcus Vance",
                    authorUsername = "glitch_reaper",
                    authorAvatarDrawable = null,
                    authorRank = "Tier 1 Champion",
                    content = "Thanks! Clutch-kick right before the transition helped maintain high revs and throttle angle.",
                    timestamp = now - (4 * 60 * 60 * 1000L),
                    parentCommentId = "c_105_1",
                    replyToUsername = "pixel_ninja",
                    likesCount = 18,
                    isLiked = false
                )
            )
            commentDao.insertComments(seedComments)
        }
    }
}
