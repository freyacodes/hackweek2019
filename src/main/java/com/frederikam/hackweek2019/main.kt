package com.frederikam.hackweek2019

import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder

private val commandManager = CommandManager()

fun main() {
    val jda = jdaBuilder().build()
    commandManager.jda = jda
    jda.restart()
}

fun jdaBuilder() = DefaultShardManagerBuilder().apply {
    setToken(System.getenv("DISCORD_TOKEN"))
    addEventListeners(commandManager)
}