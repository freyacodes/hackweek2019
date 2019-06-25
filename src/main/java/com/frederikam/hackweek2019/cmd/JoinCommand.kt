package com.frederikam.hackweek2019.cmd

import com.frederikam.hackweek2019.CommandContext
import com.frederikam.hackweek2019.util.escapeAndDefuse
import net.dv8tion.jda.api.Permission.VOICE_CONNECT
import net.dv8tion.jda.api.Permission.VOICE_SPEAK
import net.dv8tion.jda.api.entities.VoiceChannel

class JoinCommand : Command {

    override fun CommandContext.invoke() {
        join()?.let {
            replyAsync("Joining ${it.name.escapeAndDefuse()}")
        }
    }

    fun CommandContext.join(): VoiceChannel? {
        val targetChannel = invoker.voiceState?.channel
        return if (targetChannel == null) {
            replyAsync("You must be in a channel."); null
        } else if (!selfMember.hasPermission(targetChannel, VOICE_CONNECT, VOICE_SPEAK)) {
            replyAsync("I don't have permissions to use that channel."); null
        } else {
            guild.audioManager.openAudioConnection(targetChannel); targetChannel
        }
    }
}