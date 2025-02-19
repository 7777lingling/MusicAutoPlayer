package com.musicautoplayer.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.musicautoplayer.model.KeyboardConfig
import com.musicautoplayer.model.Note
import com.musicautoplayer.model.PianoKey
import com.musicautoplayer.utils.TouchEventGenerator

class AutoPlayService : AccessibilityService() {
    private var touchEventGenerator: TouchEventGenerator? = null
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        
        // 創建預設鍵盤配置（實際應用中應從設定檔讀取）
        val defaultConfig = KeyboardConfig(
            keys = listOf(
                PianoKey(60, 100, 500),  // 中央 C
                PianoKey(62, 200, 500),  // D
                PianoKey(64, 300, 500)   // E
            )
        )
        
        touchEventGenerator = TouchEventGenerator(this, defaultConfig)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 處理無障礙事件
    }

    override fun onInterrupt() {
        touchEventGenerator?.stop()
    }

    /**
     * 開始播放音符序列
     */
    fun playNotes(notes: List<Note>, onComplete: () -> Unit = {}) {
        touchEventGenerator?.play(notes, onComplete)
    }

    /**
     * 停止播放
     */
    fun stopPlaying() {
        touchEventGenerator?.stop()
    }
} 