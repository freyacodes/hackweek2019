package com.frederikam.hackweek2019.cmd

import com.frederikam.hackweek2019.CommandContext
import com.frederikam.hackweek2019.CommandManager
import com.frederikam.hackweek2019.audio.MixPlayer
import com.frederikam.hackweek2019.audio.audioPlayerManager
import com.frederikam.hackweek2019.audio.getOrCreatePlayer
import com.frederikam.hackweek2019.util.escapeAndDefuse
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PlayCommand : Command {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(PlayCommand::class.java)
    }
    private val joinCommand = JoinCommand()
    private val whitespace = "\\s".toRegex()

    override fun CommandContext.invoke() {
        joinCommand.run { join() } ?: return // Ensure we join a channel first
        val identifier = message.contentRaw.split(whitespace, limit = 2).getOrNull(1)?.trim()
        if (identifier == null) {
            replyAsync("Proper usage:\n`${CommandManager.prefix}play <url>`")
            return
        }
        val player = getOrCreatePlayer(this)
        audioPlayerManager.loadItem(identifier, AudioLoader(this, player, identifier))
    }

    private inner class AudioLoader(
            private val context: CommandContext,
            private val player: MixPlayer,
            private val identifier: String
    ) : AudioLoadResultHandler {
        override fun loadFailed(exception: FriendlyException) {
            log.error("Load failed", exception)
            context.replyAsync("Failed to load ${identifier.escapeAndDefuse()}")
        }

        override fun trackLoaded(track: AudioTrack) {
            context.replyAsync("Adding **${track.info.title.escapeAndDefuse()}** to the player")
            player.queue(track)
        }

        override fun noMatches() {
            context.replyAsync("Nothing fround for **${identifier.escapeAndDefuse()}**")
        }

        override fun playlistLoaded(playlist: AudioPlaylist) {
            if (playlist.isSearchResult) {
                context.replyAsync("I'm too lazy to add search support")
                return
            }
            context.replyAsync("Adding **${playlist.name.escapeAndDefuse()}** with **${playlist.tracks.size}** tracks.")
            playlist.tracks.forEach { player.queue(it) }
        }

    }
}