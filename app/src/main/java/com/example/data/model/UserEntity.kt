package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String, // Unique username (e.g. "neon_valkyrie")
    val displayName: String,
    val bio: String,
    val avatarDrawableName: String?, // Optional preset avatar drawable name
    val customAvatarUri: String? = null, // Optional user-uploaded image URI from device photo picker
    val bannerColorHex: Long = 0xFF6C5CE7,
    val favoriteGame: String = "Cyberpunk Arena",
    val gamingRank: String = "Apex Predator",
    val websiteUrl: String = "https://daman.music.share/valkyrie",
    val followersCount: Int = 14200,
    val followingCount: Int = 284,
    val totalLikesCount: Int = 89500,
    val isCurrentUser: Boolean = false
)
