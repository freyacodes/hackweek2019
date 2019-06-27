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