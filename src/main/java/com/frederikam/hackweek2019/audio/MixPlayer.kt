package com.frederikam.hackweek2019.audio

import com.frederikam.hackweek2019.CommandContext
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import net.dv8tion.jda.api.audio.AudioSendHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue

class MixPlayer(audioPlayerManager: AudioPlayerManager, context: CommandContext) : AudioSendHandler {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(MixPlayer::class.java)
    }

    val playing: Int get() = players.count { !it.isPaused && it.playingTrack != null }
    private val guildId = context.guild.idLong
    private val mixer = Mixer()
    val queue = ConcurrentLinkedQueue<AudioTrack>()
    val players = mutableListOf<AudioPlayer>().apply {
        for (i in 1..4) {
            add(audioPlayerManager.createPlayer())
        }
    }
    private val providers = players.map { FrameProvider(it) }
    private val playerListener = PlayerListener()
    private var _boolean = false
    var paused: Boolean
        get() = _boolean
        set(value) {
            _boolean = value
            players.forEach { it.isPaused = value }
        }
    private var lastData: ByteArray? = null

    init {
        context.guild.audioManager.sendingHandler = this
        players.forEach {
            it.addListener(playerListener)
        }
    }

    private fun getData(): ByteArray? {
        mixer.reset()

        for (sound in providers) {
            val frame = sound.player.provide()

            if (frame != null) {
                mixer.add(frame)
            } else {
            }
        }

        return mixer.get()
    }

    fun queue(track: AudioTrack) {
        players.find { it.playingTrack == null }?.apply {
            playTrack(track)
            return
        }
        queue.add(track)
    }

    override fun canProvide(): Boolean {
        checkFrameData()
        return lastData != null
    }

    override fun provide20MsAudio(): ByteBuffer? {
        checkFrameData()

        val data = lastData
        lastData = null

        return ByteBuffer.wrap(data!!)
    }

    override fun isOpus(): Boolean {
        return false
    }

    private fun checkFrameData() {
        if (lastData == null) {
            lastData = getData()
        }
    }

    fun skip(range: IntRange): Int = players
            .filterIndexed { i, p -> range.contains(i) && p.playingTrack != null }
            .map { it.stopTrack() }
            .count()

    fun destroy() {
        players.forEach { it.destroy() }
    }

    override fun toString(): String {
        return "MixPlayer[$guildId]"
    }

    inner class PlayerListener : AudioEventAdapter() {
        override fun onTrackException(player: AudioPlayer, track: AudioTrack, exception: FriendlyException) {
            log.error("Exception while playing $track", exception)
        }

        override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
            log.info("${this@MixPlayer} finished playing $track with reason $endReason")
            if (!endReason.mayStartNext) return
            val newTrack = queue.poll()
            if (newTrack != null) player.playTrack(newTrack)
        }

        override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
            log.info("${this@MixPlayer} started playing $track")
        }
    }
}