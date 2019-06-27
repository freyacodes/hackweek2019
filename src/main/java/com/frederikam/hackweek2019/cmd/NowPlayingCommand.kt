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
                append("`[${i+1}]` ")
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