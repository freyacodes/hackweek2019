package com.frederikam.hackweek2019

import com.frederikam.hackweek2019.cmd.Command
import com.frederikam.hackweek2019.cmd.JoinCommand
import com.frederikam.hackweek2019.cmd.NowPlayingCommand
import com.frederikam.hackweek2019.cmd.PlayCommand
import net.dv8tion.jda.api.events.StatusChangeEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.sharding.ShardManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CommandManager : ListenerAdapter() {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CommandManager::class.java)
        const val prefix = "::"
    }

    private val commands = hashMapOf<String, Command>()
    lateinit var jda: ShardManager

    init {
        commands["join"] = JoinCommand()
        commands["play"] = PlayCommand()
        commands["p"] = commands["play"]!!
        commands["np"] = NowPlayingCommand()
        commands["list"] = commands["np"]!!
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return
        val msg = event.message.contentRaw
        if (!msg.startsWith(prefix)) return
        val ctx = CommandContext(jda, event.message)
        val cmdName = msg.removePrefix(prefix).split(' ', limit = 2).firstOrNull() ?: return
        val cmd = commands[cmdName]
        log.info(msg)
        cmd ?: return
        cmd.invoke0(ctx)
    }

    override fun onStatusChange(event: StatusChangeEvent) {
        log.info("Status change: ${event.oldStatus} -> ${event.newStatus}")
    }
}