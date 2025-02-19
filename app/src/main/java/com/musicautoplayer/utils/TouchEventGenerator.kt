package com.musicautoplayer.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.musicautoplayer.model.KeyboardConfig
import com.musicautoplayer.model.Note
import com.musicautoplayer.model.PianoKey
import java.util.concurrent.ConcurrentHashMap

class TouchEventGenerator(
    private val service: AccessibilityService,
    private val keyboardConfig: KeyboardConfig
) {
    companion object {
        private const val TAG = "TouchEventGenerator"
        private const val TOUCH_DURATION = 100L  // 觸控持續時間（毫秒）
        private const val MAX_SIMULTANEOUS_TOUCHES = 10  // 最大同時觸控數
    }

    private val autoClicker = AutoClicker(service)
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isPlaying = false

    init {
        autoClicker.setGestureCallback(object : AutoClicker.GestureCallback {
            override fun onGestureCompleted(gestureId: Int) {
                Log.d(TAG, "手勢完成: $gestureId")
            }

            override fun onGestureCancelled(gestureId: Int) {
                Log.w(TAG, "手勢取消: $gestureId")
            }
        })
    }

    /**
     * 開始播放音符序列
     * @param notes 音符列表
     * @param onComplete 完成時的回調
     */
    fun play(notes: List<Note>, onComplete: () -> Unit) {
        if (isPlaying) return
        isPlaying = true
        autoClicker.resume()

        val sortedNotes = notes.sortedBy { it.startTime }
        var lastScheduledTime = 0L

        // 找出同時彈奏的音符
        val simultaneousNotes = mutableMapOf<Long, MutableList<Note>>()
        sortedNotes.forEach { note ->
            simultaneousNotes.getOrPut(note.startTime) { mutableListOf() }.add(note)
        }

        // 處理每組同時彈奏的音符
        simultaneousNotes.forEach { (startTime, notes) ->
            val keys = notes.mapNotNull { note ->
                findKeyForPitch(note.pitch)?.also { key ->
                    lastScheduledTime = maxOf(lastScheduledTime, startTime + note.duration)
                }
            }

            if (keys.isNotEmpty()) {
                if (keys.size == 1) {
                    autoClicker.click(keys.first(), startTime)
                } else {
                    autoClicker.multiClick(keys, startTime)
                }
            }
        }

        // 排程完成回調
        mainHandler.postDelayed({
            stop()
            onComplete()
        }, lastScheduledTime)
    }

    /**
     * 停止播放
     */
    fun stop() {
        isPlaying = false
        autoClicker.stop()
        mainHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 根據音高找到對應的鍵盤按鍵
     */
    private fun findKeyForPitch(pitch: Int): PianoKey? {
        return keyboardConfig.keys.find { it.pitch == pitch }
    }

    /**
     * 檢查是否正在播放
     */
    fun isPlaying(): Boolean = isPlaying
} 