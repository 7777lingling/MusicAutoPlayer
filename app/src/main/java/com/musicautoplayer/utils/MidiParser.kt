package com.musicautoplayer.utils

import com.musicautoplayer.model.Note
import java.io.File
import java.io.IOException
import javax.sound.midi.*
import kotlin.math.roundToLong

class MidiParser {
    companion object {
        private const val MICROSECONDS_PER_MINUTE = 60_000_000L
        private const val DEFAULT_TEMPO = 500000L // 120 BPM
    }

    private var currentTempo = DEFAULT_TEMPO
    private var ticksPerQuarterNote: Int = 0
    private var microsecondsPerTick: Double = 0.0

    @Throws(InvalidMidiDataException::class, IOException::class)
    fun parseMidiFile(midiFile: File): List<Note> {
        val sequence = MidiSystem.getSequence(midiFile)
        ticksPerQuarterNote = sequence.resolution
        microsecondsPerTick = currentTempo.toDouble() / ticksPerQuarterNote

        val noteEvents = mutableMapOf<Int, MutableMap<Int, NoteEvent>>()
        val notes = mutableListOf<Note>()

        sequence.tracks.forEach { track ->
            var currentTick = 0L
            
            for (i in 0 until track.size()) {
                val event = track[i]
                currentTick += event.tick

                if (event.message is MetaMessage) {
                    handleMetaMessage(event.message as MetaMessage)
                    continue
                }

                if (event.message !is ShortMessage) continue
                
                val message = event.message as ShortMessage
                val channel = message.channel

                when (message.command) {
                    ShortMessage.NOTE_ON -> {
                        if (message.data2 > 0) { // Note On with velocity > 0
                            val noteEvent = NoteEvent(
                                pitch = message.data1,
                                velocity = message.data2,
                                startTick = currentTick
                            )
                            noteEvents.getOrPut(channel) { mutableMapOf() }[message.data1] = noteEvent
                        } else { // Note On with velocity 0 is treated as Note Off
                            handleNoteOff(channel, message.data1, currentTick, noteEvents, notes)
                        }
                    }
                    ShortMessage.NOTE_OFF -> {
                        handleNoteOff(channel, message.data1, currentTick, noteEvents, notes)
                    }
                }
            }
        }

        return notes.sortedBy { it.startTime }
    }

    private fun handleMetaMessage(message: MetaMessage) {
        if (message.type == 0x51) { // Tempo change
            val data = message.data
            currentTempo = ((data[0].toInt() and 0xFF) shl 16) +
                    ((data[1].toInt() and 0xFF) shl 8) +
                    (data[2].toInt() and 0xFF)
            microsecondsPerTick = currentTempo.toDouble() / ticksPerQuarterNote
        }
    }

    private fun handleNoteOff(
        channel: Int,
        pitch: Int,
        currentTick: Long,
        noteEvents: MutableMap<Int, MutableMap<Int, NoteEvent>>,
        notes: MutableList<Note>
    ) {
        noteEvents[channel]?.remove(pitch)?.let { noteEvent ->
            val startTime = (noteEvent.startTick * microsecondsPerTick / 1000).roundToLong()
            val duration = ((currentTick - noteEvent.startTick) * microsecondsPerTick / 1000).roundToLong()
            
            notes.add(Note(
                pitch = noteEvent.pitch,
                startTime = startTime,
                duration = duration,
                velocity = noteEvent.velocity
            ))
        }
    }

    private data class NoteEvent(
        val pitch: Int,
        val velocity: Int,
        val startTick: Long
    )
} 