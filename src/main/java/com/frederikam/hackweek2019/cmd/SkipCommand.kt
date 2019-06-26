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
            val range = found.groups[1]!!.value.toInt()..found.groups[2]!!.value.toInt()
            player.skip(range)
        } else {
            val num = numberRegex.find(prefixStripped)?.value?.toInt()
            if (num != null) player.skip(num..num)
            else 0
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