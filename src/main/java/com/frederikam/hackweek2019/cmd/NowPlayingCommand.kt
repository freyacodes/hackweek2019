package com.frederikam.hackweek2019.cmd

import com.frederikam.hackweek2019.CommandContext
import com.frederikam.hackweek2019.audio.getOrCreatePlayer
import com.frederikam.hackweek2019.util.escapeAndDefuse

class NowPlayingCommand : Command {

    override fun CommandContext.invoke() {
        val player = getOrCreatePlayer()
        if (player.playing == 0) {
            replyAsync("Not playing anything"); return
        }

        replyAsync(buildString {
            appendln("There are `${player.playing}/${player.players.size}` players are playing:")
            player.players.forEachIndexed { i, ap ->
                append("`[$i+1]` ")
                val track = ap.playingTrack
                if (track == null) appendln("(idle)")
                else appendln("**${track.info.title.escapeAndDefuse()}**")
            }
            if (player.queue.isNotEmpty()) {
                appendln()
                appendln("The queue has `${player.queue.size}` tracks remaining.")
            }
        })
    }
}