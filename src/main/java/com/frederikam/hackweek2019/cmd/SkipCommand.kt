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
import com.frederikam.hackweek2019.audio.getOrCreatePlayer

class SkipCommand : Command {

    val rangeRegex = "(\\d+)-(\\d+)".toRegex()
    val numberRegex = "\\d+".toRegex()

    override fun CommandContext.invoke() {
        val player = getOrCreatePlayer()

        val found = rangeRegex.find(prefixStripped)
        val skipped = if (found != null) {
            val from = found.groups[1]!!.value.toInt() - 1
            val to   = found.groups[2]!!.value.toInt() - 1
            player.skip(from..to)
        } else {
            var num = numberRegex.find(prefixStripped)?.value?.toInt()
            if (num != null) {
                num--
                player.skip(num..num)
            }  else 0
        }

        if (skipped == 0) {
            val prefix = CommandManager.prefix
            replyAsync("Nothing skipped. Proper usage:\n```"
                    + "\n${prefix}skip 2"
                    + "\n${prefix}skip 2-4"
                    + "\n```")
        } else {
            val plurality = if(skipped == 1) "player" else "players"
            replyAsync("Skipped **$skipped** $plurality")
        }
    }
}