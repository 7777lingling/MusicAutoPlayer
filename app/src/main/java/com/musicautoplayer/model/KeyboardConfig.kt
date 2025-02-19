package com.musicautoplayer.model

data class KeyboardConfig(
    val keys: List<PianoKey>,
    val name: String = "預設配置",
    val description: String = ""
)

data class PianoKey(
    val pitch: Int,          // MIDI 音高
    val x: Int,              // 觸控點 X 座標
    val y: Int,              // 觸控點 Y 座標
    val width: Int = 100,    // 按鍵寬度
    val height: Int = 150    // 按鍵高度
) {
    fun contains(touchX: Int, touchY: Int): Boolean {
        return touchX >= x && touchX <= x + width &&
               touchY >= y && touchY <= y + height
    }
} 