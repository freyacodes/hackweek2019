package com.frederikam.hackweek2019.util

import java.util.*



object TextUtils {

    private val BACKTICK = Collections.singleton('`')
    private val MARKDOWN_CHARS = listOf('*', '`', '~', '_', '|')
    private const val ZERO_WIDTH_CHAR = '\u200b'

    private fun escape(input: String, toEscape: Collection<Char>): String {
        val revisedString = StringBuilder(input.length)
        for (n in input.toCharArray()) {
            if (toEscape.contains(n)) {
                revisedString.append("\\")
            }
            revisedString.append(n)
        }
        return revisedString.toString()
    }

    fun escapeMarkdown(input: String): String {
        return escape(input, MARKDOWN_CHARS)
    }

    fun escapeBackticks(input: String): String {
        return escape(input, BACKTICK)
    }

    /**
     * @return the input, with escaped markdown and defused mentions and URLs
     * It is a good idea to use this on any user generated values that we reply in plain text.
     */
    fun escapeAndDefuse(input: String): String {
        return defuse(escapeMarkdown(input))
    }

    /**
     * Defuses some content that Discord couldn't know wasn't our intention.
     *
     *
     * When the nickname contains a link, or a mention, the bot uses that as-is in the text.
     * Since Discord can't know the bot didn't mean to do that, we escape it so it will not be interpreted.
     *
     * @param input the string to escape, e.g. track titles, nicknames, supplied values
     * @return defused content
     */
    fun defuse(input: String): String {
        return defuseUrls(defuseMentions(input))
    }

    private fun defuseMentions(input: String): String {
        return input.replace("@here".toRegex(), "@" + ZERO_WIDTH_CHAR + "here")
                .replace("@everyone".toRegex(), "@" + ZERO_WIDTH_CHAR + "everyone")
    }

    private fun defuseUrls(input: String): String {
        return input.replace("://".toRegex(), ":$ZERO_WIDTH_CHAR//")
    }

}

fun String.escapeAndDefuse() = TextUtils.escapeAndDefuse(this)
fun String.defuse() = TextUtils.defuse(this)
fun String.escapeMarkdown() = TextUtils.escapeMarkdown(this)
fun String.escapeBackticks() = TextUtils.escapeBackticks(this)