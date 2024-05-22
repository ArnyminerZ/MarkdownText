package com.arnyminerz.markdowntext

import android.util.Log

object Logger {
    enum class Level {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        ASSERT
    }

    private const val TAG = "MarkdownText"

    private var logLevel: Level = Level.INFO

    /**
     * Sets the log level.
     * @param level The new log level.
     */
    fun setLogLevel(level: Level) {
        logLevel = level
    }

    /**
     * Logs a message with the specified level.
     *
     * Won't log if the level is lower than the current log level.
     * @param level The level of the message.
     * @param message The message to log.
     */
    fun log(level: Level, message: String) {
        // Check log level
        if (level < logLevel) return
        // Log message
        when (level) {
            Level.VERBOSE -> Log.v(TAG, message)
            Level.DEBUG -> Log.d(TAG, message)
            Level.INFO -> Log.i(TAG, message)
            Level.WARN -> Log.w(TAG, message)
            Level.ERROR -> Log.e(TAG, message)
            Level.ASSERT -> Log.wtf(TAG, message)
        }
    }

    /**
     * Logs a message with the specified level, prepending the given amount ([depth]) of [prefix]es.
     *
     * Won't log if the level is lower than the current log level.
     * @param level The level of the message.
     * @param message The message to log.
     */
    fun log(level: Level, message: String, depth: Int = 0, prefix: String = "  ") {
        val prepend = prefix.repeat(depth)
        log(level, "$prepend$message")
    }

    /**
     * Logs a message with the VERBOSE level.
     * @param message The message to log.
     */
    fun verbose(message: String = "", depth: Int = 0, prefix: String = "  ") {
        log(Level.VERBOSE, message, depth, prefix)
    }

    /**
     * Logs a message with the DEBUG level.
     * @param message The message to log.
     */
    fun debug(message: String = "", depth: Int = 0, prefix: String = "  ") {
        log(Level.DEBUG, message, depth, prefix)
    }
}
