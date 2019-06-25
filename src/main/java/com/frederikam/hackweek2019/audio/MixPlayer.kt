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
import java.util.concurrent.Executors

class MixPlayer(audioPlayerManager: AudioPlayerManager, context: CommandContext) : AudioSendHandler {

    companion object {
        /** 2 seconds of 20ms frames */
        private const val MAX_BUFFER_SIZE = (1000 / 20) * 2
        /** Ignore players falling behind if the buffer is shorter than this */
        private const val LOW_BUFFER_THRESHOLD = 1000 / 20
        /** Affects how often the buffer is refilled */
        private const val BUFFER_REFILL_DELAY_MS = 250L
        private val log: Logger = LoggerFactory.getLogger(MixPlayer::class.java)
    }

    private val jda = context.jda
    private val guildId = context.guild.idLong
    private val guild get() = jda.getGuildById(guildId)
    private val buffer = ConcurrentLinkedQueue<ByteBuffer>() // 20ms each
    private val executor = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "mixer-$guild").apply { isDaemon = true }
    }

    private val queue = ConcurrentLinkedQueue<AudioTrack>()
    private val p1 = audioPlayerManager.createPlayer()
    private val p2 = audioPlayerManager.createPlayer()
    private val p3 = audioPlayerManager.createPlayer()
    private val p4 = audioPlayerManager.createPlayer()
    private val players = listOf(p1, p2, p3, p4)
    private val mixer = Mixer()
    private val playerListener = PlayerListener()
    private var _boolean = false
    var paused: Boolean
        get() = _boolean
        set(value) {
            _boolean = value
            players.forEach { it.isPaused = value }
        }

    init {
        context.guild.audioManager.sendingHandler = this
        players.forEach {
            it.addListener(playerListener)
        }
    }

    override fun provide20MsAudio() = buffer.remove()!!
    override fun canProvide() = buffer.isNotEmpty()

    fun destroy() {
        executor.shutdown()
        players.forEach { it.destroy() }
    }

    fun queue(track: AudioTrack) {
        players.find { it.playingTrack == null }?.apply {
            playTrack(track)
            return
        }
        queue.add(track)
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