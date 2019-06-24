package com.frederikam.hackweek2019

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

data class CommandContext(val message: Message) {
    val guild: Guild get() = message.guild
    val channel: TextChannel get() = message.channel as TextChannel
}