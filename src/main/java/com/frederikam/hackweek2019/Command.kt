package com.frederikam.hackweek2019

interface Command {

    fun invoke(ctx: CommandContext)

}