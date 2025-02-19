package com.musicautoplayer.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.musicautoplayer.model.PianoKey
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class AutoClicker(private val service: AccessibilityService) {
    companion object {
        private const val TAG = "AutoClicker"
        private const val DEFAULT_PRESS_DURATION = 50L    // 預設按壓時間（毫秒）
        private const val MAX_GESTURES = 10               // 最大同時手勢數
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private val activeGestures = ConcurrentHashMap<Int, GestureDescription>()
    private val isEnabled = AtomicBoolean(true)
    private var gestureCallback: GestureCallback? = null

    /**
     * 執行單次點擊
     * @param key 要點擊的按鍵
     * @param delay 延遲時間（毫秒）
     * @param duration 按壓時間（毫秒）
     */
    fun click(
        key: PianoKey,
        delay: Long = 0,
        duration: Long = DEFAULT_PRESS_DURATION,
        gestureId: Int = key.hashCode()
    ) {
        if (!isEnabled.get()) return

        mainHandler.postDelayed({
            if (!isEnabled.get()) return@postDelayed

            val path = Path().apply {
                // 移動到按鍵中心點
                moveTo(key.x + key.width / 2f, key.y + key.height / 2f)
            }

            val stroke = GestureDescription.StrokeDescription(
                path,
                0,      // 開始時間偏移
                duration
            )

            val gesture = GestureDescription.Builder()
                .addStroke(stroke)
                .build()

            performGesture(gesture, gestureId)
        }, delay)
    }

    /**
     * 執行多點同時點擊
     * @param keys 要同時點擊的按鍵列表
     */
    fun multiClick(keys: List<PianoKey>, delay: Long = 0) {
        if (keys.size > MAX_GESTURES) {
            Log.w(TAG, "同時點擊數超過上限: ${keys.size} > $MAX_GESTURES")
            return
        }

        keys.forEach { key ->
            click(key, delay)
        }
    }

    /**
     * 執行手勢
     */
    private fun performGesture(gesture: GestureDescription, gestureId: Int) {
        activeGestures[gestureId] = gesture

        service.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    activeGestures.remove(gestureId)
                    gestureCallback?.onGestureCompleted(gestureId)
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    activeGestures.remove(gestureId)
                    gestureCallback?.onGestureCancelled(gestureId)
                }
            },
            null
        )
    }

    /**
     * 停止所有點擊
     */
    fun stop() {
        isEnabled.set(false)
        mainHandler.removeCallbacksAndMessages(null)
        activeGestures.clear()
    }

    /**
     * 恢復點擊功能
     */
    fun resume() {
        isEnabled.set(true)
    }

    /**
     * 設定手勢回調
     */
    fun setGestureCallback(callback: GestureCallback) {
        this.gestureCallback = callback
    }

    /**
     * 手勢回調介面
     */
    interface GestureCallback {
        fun onGestureCompleted(gestureId: Int)
        fun onGestureCancelled(gestureId: Int)
    }
} 