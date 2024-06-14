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
    fun log(level: Level, message: String, vararg args: Any?) {
        // Check log level
        if (level < logLevel) return
        // Log message
        when (level) {
            Level.VERBOSE -> Log.v(TAG, message.format(*args))
            Level.DEBUG -> Log.d(TAG, message.format(*args))
            Level.INFO -> Log.i(TAG, message.format(*args))
            Level.WARN -> Log.w(TAG, message.format(*args))
            Level.ERROR -> Log.e(TAG, message.format(*args))
            Level.ASSERT -> Log.wtf(TAG, message.format(*args))
        }
    }

    /**
     * Logs a message with the specified level, prepending the given amount ([depth]) of [prefix]es.
     *
     * Won't log if the level is lower than the current log level.
     * @param level The level of the message.
     * @param message The message to log.
     */
    fun log(level: Level, message: String, vararg args: Any?, depth: Int = 0, prefix: String = "  ") {
        val prepend = prefix.repeat(depth)
        log(level, "$prepend$message", *args)
    }

    /**
     * Logs a message with the `VERBOSE` level.
     * @param message The message to log.
     */
    fun verbose(message: String = "", vararg args: Any?, depth: Int = 0, prefix: String = "  ") {
        log(Level.VERBOSE, message, *args, depth = depth, prefix = prefix)
    }

    /**
     * Logs a message with the `DEBUG` level.
     * @param message The message to log.
     */
    fun debug(message: String = "", vararg args: Any?, depth: Int = 0, prefix: String = "  ") {
        log(Level.DEBUG, message, *args, depth = depth, prefix = prefix)
    }

    /**
     * Logs a message with the `INFO` level.
     * @param message The message to log.
     */
    fun info(message: String = "", vararg args: Any?, depth: Int = 0, prefix: String = "  ") {
        log(Level.INFO, message, *args, depth = depth, prefix = prefix)
    }

    /**
     * Logs a message with the `WARNING` level.
     * @param message The message to log.
     */
    fun warning(message: String = "", vararg args: Any?, depth: Int = 0, prefix: String = "  ") {
        log(Level.WARN, message, *args, depth = depth, prefix = prefix)
    }

    /**
     * Logs a message with the `ERROR` level.
     * @param message The message to log.
     */
    fun error(message: String = "", vararg args: Any?, depth: Int = 0, prefix: String = "  ") {
        log(Level.ERROR, message, *args, depth = depth, prefix = prefix)
    }

    /**
     * Logs a message with the `ASSERT` level.
     * @param message The message to log.
     */
    fun assert(message: String = "", vararg args: Any?, depth: Int = 0, prefix: String = "  ") {
        log(Level.ASSERT, message, *args, depth = depth, prefix = prefix)
    }
}
