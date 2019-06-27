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

    override fun CommandContext.invoke() {
        joinCommand.run { join() } ?: return // Ensure we join a channel first
        val identifier = prefixStripped
        if (identifier.isEmpty()) {
            replyAsync("Proper usage:\n`${CommandManager.prefix}play <url>`")
            return
        }
        val player = getOrCreatePlayer()
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