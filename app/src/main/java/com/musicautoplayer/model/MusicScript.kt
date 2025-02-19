package com.musicautoplayer.model

data class MusicScript(
    val notes: List<Note>,
    val metadata: ScriptMetadata = ScriptMetadata()
)

data class ScriptMetadata(
    val version: String = "1.0",
    val createdAt: Long = System.currentTimeMillis(),
    val name: String = "未命名腳本",
    val description: String = ""
) 