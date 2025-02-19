package com.musicautoplayer.model

import java.util.Date

data class MusicSheet(
    val id: Long = 0,
    val name: String,
    val filePath: String,
    val dateCreated: Date,
    val description: String = "",
    val totalNotes: Int = 0,
    val duration: Long = 0    // 總時長（毫秒）
) 