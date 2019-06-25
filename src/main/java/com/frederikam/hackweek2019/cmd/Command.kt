package com.frederikam.hackweek2019.cmd

import com.frederikam.hackweek2019.CommandContext

interface Command {

    fun invoke0(ctx: CommandContext) { ctx.invoke() }
    fun CommandContext.invoke()

}