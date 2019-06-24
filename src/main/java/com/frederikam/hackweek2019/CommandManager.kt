package com.frederikam.hackweek2019

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.sharding.ShardManager

class CommandManager : ListenerAdapter() {

    lateinit var jda: ShardManager

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return
        val ctx = CommandContext(event.message)
    }
}