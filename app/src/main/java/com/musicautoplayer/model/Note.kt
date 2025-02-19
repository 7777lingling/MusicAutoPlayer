package com.musicautoplayer.model

data class Note(
    val pitch: Int,        // MIDI 音高 (0-127)
    val startTime: Long,   // 開始時間 (毫秒)
    val duration: Long,    // 持續時間 (毫秒)
    val velocity: Int      // 力度 (0-127)
) 