package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val userId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarDrawable: String?,
    val authorRank: String = "Gamer",
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val parentCommentId: String? = null, // null for root comment; parent comment id for replies
    val replyToUsername: String? = null, // @username of recipient being replied to
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)
