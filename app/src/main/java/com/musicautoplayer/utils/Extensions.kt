package com.musicautoplayer.utils

import com.google.gson.Gson
import com.musicautoplayer.model.Note

fun List<Note>.toJson(): String = Gson().toJson(this)

// 將音符轉換為 DoReMi 表示法
fun Note.toDoReMi(): String {
    val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val octave = (pitch / 12) - 1
    val noteName = noteNames[pitch % 12]
    return "$noteName$octave"
} 