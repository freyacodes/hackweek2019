/*
 * MIT License
 *
 * Copyright (c) 2019 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.frederikam.hackweek2019

import com.frederikam.hackweek2019.audio.destroyPlayer
import com.frederikam.hackweek2019.audio.players
import com.frederikam.hackweek2019.cmd.*
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.StatusChangeEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.sharding.ShardManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.math.absoluteValue

class CommandManager : ListenerAdapter() {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CommandManager::class.java)
        const val prefix = "::"
    }

    private val random = Random()
    private val commands = hashMapOf<String, Command>()
    lateinit var jda: ShardManager

    init {
        commands["help"] = HelpCommand()
        commands["join"] = JoinCommand()
        commands["play"] = PlayCommand()
        commands["p"] = commands["play"]!!
        commands["np"] = NowPlayingCommand()
        commands["list"] = commands["np"]!!
        commands["skip"] = SkipCommand()
        commands["s"] = commands["skip"]!!
        commands["stop"] = StopCommand()
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

        try {
            cmd.invoke0(ctx)
        } catch (e: Exception) {
            val code = Random().nextInt().absoluteValue
            log.error("Exception during command invocation. Ref=$code", e)
            ctx.replyAsync("An error occurred! Reference code `$code`")
        }
    }

    override fun onStatusChange(event: StatusChangeEvent) {
        log.info("Status change: ${event.oldStatus} -> ${event.newStatus}")
    }

    override fun onGuildJoin(event: GuildJoinEvent) {
        log.info("Joined ${event.guild}")
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        log.info("Left ${event.guild}")
        destroyPlayer(event.guild.idLong)
    }

    private val VoiceChannel.isOurs: Boolean get() = members.any { it.guild.selfMember == it }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        if (event.channelJoined.isOurs && !event.member.user.isBot) {
            players[event.guild.idLong]?.paused = false
        }
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        if (event.channelLeft.isOurs
                && !event.channelLeft.members.any { !it.user.isBot }) {
            players[event.guild.idLong]?.paused = true
        }
    }
}