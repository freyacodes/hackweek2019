package com.frederikam.hackweek2019.cmd

import com.frederikam.hackweek2019.CommandContext
import com.frederikam.hackweek2019.audio.getOrCreatePlayer

class PlayCommand : Command {

    private val joinCommand = JoinCommand()

    override fun CommandContext.invoke() {
        val currentChannel = joinCommand.run { join() } ?: return
        val player = getOrCreatePlayer(this)
        val identifier = message.contentRaw.split(' ', limit = 1).getOrNull(1)?.trim()
        
    }
}