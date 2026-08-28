package com.example.data.model

data class DamanTrack(
    val id: String,
    val title: String,
    val artist: String,
    val genre: String,
    val duration: String,
    val bpm: Int,
    val usageCount: String,
    val shareUrl: String,
    val coverGradientStart: Long,
    val coverGradientEnd: Long
)
