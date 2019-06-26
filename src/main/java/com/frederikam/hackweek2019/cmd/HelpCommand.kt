package com.frederikam.hackweek2019.cmd

import com.frederikam.hackweek2019.CommandContext

class HelpCommand : Command {

    companion object {
        val helpText = """
```
::help
::join
::play https://www.youtube.com/watch?v=dQw4w9WgXcQ
::skip 2
::skip 2-4
::stop
```

https://hackweek.fredboat.com/"""
    }

    override fun CommandContext.invoke() {
        replyAsync(helpText)
    }
}