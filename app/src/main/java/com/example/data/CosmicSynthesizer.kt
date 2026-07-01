package com.example.data

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.sin

object CosmicSynthesizer {
    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private var isPlaying = false
    
    // Soothing pentatonic scale frequencies (Tian Dao harmony)
    private val scale = doubleArrayOf(
        220.0,  // A3 (Yin base)
        247.5,  // B3
        293.33, // D4
        330.0,  // E4 (Balance)
        396.0,  // G4
        440.0,  // A4 (Yang crest)
        495.0,  // B4
        586.66, // D5
        660.0   // E5
    )
    
    private var currentFrequency = 220.0
    private var targetFrequency = 220.0
    private var volumeMultiplier = 0.5f

    fun start() {
        if (isPlaying) return
        isPlaying = true
        
        val sampleRate = 22050
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        
        val bufferSize = Math.max(minBufferSize, 2048)
        
        try {
            audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
            )
            audioTrack?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        synthJob = CoroutineScope(Dispatchers.Default).launch {
            val shortBuffer = ShortArray(1024)
            var phase = 0.0
            
            while (isActive && isPlaying) {
                // Portamento sliding of frequencies for clean transition
                currentFrequency += (targetFrequency - currentFrequency) * 0.08
                
                for (i in shortBuffer.indices) {
                    val angle = 2.0 * Math.PI * currentFrequency / sampleRate
                    phase += angle
                    if (phase > 2.0 * Math.PI) {
                        phase -= 2.0 * Math.PI
                    }
                    
                    // Core sine wave with organic third harmonic overlay
                    val primary = sin(phase)
                    val harmonic = 0.25 * sin(3.0 * phase)
                    val sampleVal = ((primary + harmonic) / 1.25 * Short.MAX_VALUE * volumeMultiplier).toInt()
                    
                    shortBuffer[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                
                try {
                    audioTrack?.write(shortBuffer, 0, shortBuffer.size)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                yield()
            }
        }
    }
    
    fun stop() {
        isPlaying = false
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
    }
    
    fun setVolume(vol: Float) {
        volumeMultiplier = vol.coerceIn(0f, 1f)
    }

    fun setNoteIndex(index: Int) {
        val safeIndex = index.coerceIn(0, scale.lastIndex)
        targetFrequency = scale[safeIndex]
    }
}
