package com.frederikam.hackweek2019.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue

class MixPlayer(private val audioPlayerManager: AudioPlayerManager, guild: Long) : AudioSendHandler {

    private val buffer = ConcurrentLinkedQueue<ByteBuffer>()

    private val p1 = audioPlayerManager.createPlayer()
    private val p2 = audioPlayerManager.createPlayer()
    private val p3 = audioPlayerManager.createPlayer()
    private val p4 = audioPlayerManager.createPlayer()
    private val players = listOf(p1, p2, p3, p4)

    init {

    }

    override fun provide20MsAudio() = buffer.remove()!!
    override fun canProvide() = buffer.isNotEmpty()
}