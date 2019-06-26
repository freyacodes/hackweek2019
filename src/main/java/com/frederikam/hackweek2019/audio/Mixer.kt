package com.frederikam.hackweek2019.audio

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import java.nio.ByteBuffer
import java.nio.ByteOrder.BIG_ENDIAN
import kotlin.math.abs
import kotlin.math.max

/**
 * Original author: Sedmelluq
 * Heavily modified by Frederikam
 *
 * https://gist.github.com/sedmelluq/d13f84a8b4bd31bc22a633dd70b5b985
 */
class Mixer {
    private val inputFormat = StandardAudioDataFormats.DISCORD_PCM_S16_BE
    private val mixBuffer: IntArray = IntArray(inputFormat.chunkSampleCount * 2)
    private val outputBuffer: ByteArray = ByteArray(inputFormat.totalSampleCount() * 4)
    private val wrappedOutput = ByteBuffer.wrap(outputBuffer).order(BIG_ENDIAN).asShortBuffer()
    private val previousMultiplier = Multiplier()
    private val currentMultiplier = Multiplier()
    private var onlyFrame: ByteArray? = null
    private var isEmpty = true

    fun reset() {
        isEmpty = true
        onlyFrame = null
    }

    fun add(frame: AudioFrame) {
        val data = frame.data

        if (isEmpty) {
            isEmpty = false
            onlyFrame = data
            return
        }

        if (onlyFrame != null) {
            val inputBuffer = ByteBuffer.wrap(onlyFrame!!)
                    .order(BIG_ENDIAN)
                    .asShortBuffer()

            for (i in mixBuffer.indices) {
                mixBuffer[i] = inputBuffer.get(i).toInt()
            }

            onlyFrame = null
        }

        val inputBuffer = ByteBuffer.wrap(data)
                .order(BIG_ENDIAN)
                .asShortBuffer()

        for (i in mixBuffer.indices) {
            mixBuffer[i] += inputBuffer.get(i).toInt()
        }
    }

    fun get(): ByteArray? {
        if (isEmpty) {
            previousMultiplier.reset()
            return null
        } else if (onlyFrame != null) {
            previousMultiplier.reset()
            return onlyFrame
        }

        updateMultiplier()

        if (!currentMultiplier.identity || !previousMultiplier.identity) {
            for (i in 0..9) {
                val gradientMultiplier = (currentMultiplier.value * i + previousMultiplier.value * (10 - i)) * 0.1f
                wrappedOutput.put(i, (gradientMultiplier * mixBuffer[i]).toShort())
            }

            for (i in 10 until mixBuffer.size) {
                wrappedOutput.put(i, (currentMultiplier.value * mixBuffer[i]).toShort())
            }

            previousMultiplier.identity = currentMultiplier.identity
            previousMultiplier.value = currentMultiplier.value
        } else {
            for (i in mixBuffer.indices) {
                wrappedOutput.put(i, mixBuffer[i].toShort())
            }
        }

        return outputBuffer
    }

    private fun updateMultiplier() {
        var peak = 0

        if (!isEmpty) {
            for (value in mixBuffer) {
                peak = max(peak, abs(value))
            }
        }

        if (peak > 32767) {
            currentMultiplier.identity = false
            currentMultiplier.value = 32767.0f / peak
        } else {
            currentMultiplier.identity = true
            currentMultiplier.value = 1.0f
        }
    }

    private class Multiplier {
        var identity = true
        var value = 1.0f

        fun reset() {
            identity = true
            value = 1.0f
        }
    }
}

