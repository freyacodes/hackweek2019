package com.frederikam.hackweek2019.cmd

import com.frederikam.hackweek2019.CommandContext
import com.frederikam.hackweek2019.util.escapeAndDefuse
import net.dv8tion.jda.api.Permission.VOICE_CONNECT
import net.dv8tion.jda.api.Permission.VOICE_SPEAK

class JoinCommand : Command {
    override fun CommandContext.invoke() {
        val targetChannel = invoker.voiceState?.channel
        if (targetChannel == null) {
            replyAsync("You must be in a channel."); return
        } else if (!selfMember.hasPermission(targetChannel, VOICE_CONNECT, VOICE_SPEAK)) {
            replyAsync("I don't have permissions to use that channel."); return
        }
        replyAsync("Joining ${targetChannel.name.escapeAndDefuse()}")
        guild.audioManager.openAudioConnection(targetChannel)
    }
}