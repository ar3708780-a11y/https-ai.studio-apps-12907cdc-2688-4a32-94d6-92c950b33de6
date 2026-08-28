package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val creatorId: String,
    val title: String,
    val description: String,
    val gameTitle: String,
    val thumbnailDrawableName: String, // e.g. "img_thumb_fps", "img_thumb_rpg", etc.
    val customThumbnailUri: String? = null,
    val musicTrack: String = "Daman Synthwave Anthem #01",
    val duration: String, // e.g. "04:15", "00:45"
    val isClip: Boolean, // true if short clip/highlight, false if full gameplay
    val viewsCount: Long = 1200,
    val likesCount: Long = 450,
    val isLiked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val resolution: String = "4K 60FPS"
)
