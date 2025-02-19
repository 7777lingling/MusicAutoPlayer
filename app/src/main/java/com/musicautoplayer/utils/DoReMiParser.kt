package com.musicautoplayer.utils

import com.musicautoplayer.model.Note
import java.lang.IllegalArgumentException

class DoReMiParser {
    companion object {
        private const val DEFAULT_VELOCITY = 80
        private const val DEFAULT_BPM = 120
        private const val MILLISECONDS_PER_MINUTE = 60_000L

        // 音符到 MIDI 音高的映射
        private val NOTE_TO_PITCH = mapOf(
            "C" to 0, "C#" to 1, "Db" to 1,
            "D" to 2, "D#" to 3, "Eb" to 3,
            "E" to 4,
            "F" to 5, "F#" to 6, "Gb" to 6,
            "G" to 7, "G#" to 8, "Ab" to 8,
            "A" to 9, "A#" to 10, "Bb" to 10,
            "B" to 11
        )
    }

    /**
     * 解析 DoReMi 格式的樂譜
     * @param input 樂譜字串，例如 "C4 D4 E4 R F4"
     * @param bpm 每分鐘拍數，預設 120
     * @return 音符列表
     */
    fun parse(input: String, bpm: Int = DEFAULT_BPM): List<Note> {
        val notes = mutableListOf<Note>()
        val noteDuration = MILLISECONDS_PER_MINUTE / bpm
        var currentTime = 0L

        input.trim().split("\\s+".toRegex()).forEach { noteStr ->
            if (noteStr == "R") {
                // 處理休止符
                currentTime += noteDuration
            } else {
                try {
                    val note = parseNote(noteStr, currentTime, noteDuration)
                    notes.add(note)
                    currentTime += noteDuration
                } catch (e: Exception) {
                    throw IllegalArgumentException("無效的音符格式: $noteStr", e)
                }
            }
        }

        return notes
    }

    /**
     * 解析單個音符
     * @param noteStr 音符字串，例如 "C4"
     * @param startTime 開始時間
     * @param duration 持續時間
     * @return Note 物件
     */
    private fun parseNote(noteStr: String, startTime: Long, duration: Long): Note {
        // 使用正則表達式解析音符和八度
        val matcher = "([A-G][#b]?)([0-9])".toRegex().matchEntire(noteStr)
            ?: throw IllegalArgumentException("無效的音符格式: $noteStr")

        val (noteName, octave) = matcher.destructured
        
        // 計算 MIDI 音高
        val baseNote = NOTE_TO_PITCH[noteName] 
            ?: throw IllegalArgumentException("無效的音符名稱: $noteName")
        val pitch = baseNote + (octave.toInt() + 1) * 12

        return Note(
            pitch = pitch,
            startTime = startTime,
            duration = duration,
            velocity = DEFAULT_VELOCITY
        )
    }
} 