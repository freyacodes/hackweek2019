package com.frederikam.hackweek2019

import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager

fun main() {

}

fun jdaBuilder(): ShardManager = DefaultShardManagerBuilder().apply {
    setToken(System.getenv("DISCORD_TOKEN"))
    addEventListeners(CommandManager())
}.build()