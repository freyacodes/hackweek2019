package com.frederikam.hackweek2019.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import java.util.concurrent.atomic.AtomicReference

/** Lavaplayer does not have a canProvide() method. This acts as a workaround */
class FrameProvider(val player: AudioPlayer): AudioEventAdapter() {
    private val lastFrame = AtomicReference<AudioFrame>()

    fun canProvide(): Boolean {
        if(lastFrame.get() != null) return true
        val new = player.provide()
        lastFrame.set(new)
        return new != null
    }

    fun provide(): AudioFrame? = lastFrame.getAndSet(null)

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        lastFrame.set(null)
    }
}