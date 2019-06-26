package com.frederikam.hackweek2019

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder

private val commandManager = CommandManager()

fun main() {
    val jda = jdaBuilder().build()
    commandManager.jda = jda
}

fun jdaBuilder() = DefaultShardManagerBuilder().apply {
    setToken(System.getenv("DISCORD_TOKEN"))
    addEventListeners(commandManager)
    setAudioSendFactory(NativeAudioSendFactory())
}