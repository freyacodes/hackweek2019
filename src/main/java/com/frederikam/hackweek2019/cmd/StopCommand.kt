package com.frederikam.hackweek2019.cmd

import com.frederikam.hackweek2019.CommandContext
import com.frederikam.hackweek2019.audio.destroyPlayer

class StopCommand : Command {
    override fun CommandContext.invoke() {
        guild.audioManager.closeAudioConnection()
        destroyPlayer(guild.idLong)
    }
}