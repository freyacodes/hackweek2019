package com.frederikam.hackweek2019.audio

import com.frederikam.hackweek2019.CommandContext
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import java.util.concurrent.ConcurrentHashMap

val audioPlayerManager = DefaultAudioPlayerManager().apply {
    enableGcMonitoring()
    registerSourceManager(YoutubeAudioSourceManager())
    registerSourceManager(SoundCloudAudioSourceManager())
    registerSourceManager(BandcampAudioSourceManager())
    registerSourceManager(TwitchStreamAudioSourceManager())
    configuration.outputFormat = StandardAudioDataFormats.DISCORD_PCM_S16_BE

}
val players = ConcurrentHashMap<Long, MixPlayer>()
fun getOrCreatePlayer(context: CommandContext) = players.getOrPut(context.guild.idLong) {
    MixPlayer(audioPlayerManager, context)
}