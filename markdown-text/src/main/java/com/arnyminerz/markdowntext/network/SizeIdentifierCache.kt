package com.arnyminerz.markdowntext.network

import android.content.Context
import androidx.compose.ui.unit.IntSize
import java.io.File
import java.text.Normalizer
import org.jetbrains.annotations.VisibleForTesting

object SizeIdentifierCache {
    /**
     * The duration in milliseconds that the cache will be valid for.
     * Android may clear cache automatically before if it needs to free up space.
     */
    private var cacheDurationMillis: Long = 7 * 24 * 60 * 60 * 1000 // 1 week

    /**
     * Replaces all non-alphanumeric characters with underscores and normalizes the string.
     */
    @VisibleForTesting
    fun muteUrl(url: String): String {
        // Remove the protocol
        var result = url.replace(Regex("https?://"), "")
        // Normalize the string
        result = Normalizer.normalize(result, Normalizer.Form.NFD)
        // Remove diacritics
        result = result.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        // Lowercase the string
        result = result.lowercase()
        // Replace all non-alphanumeric characters with underscores
        result = result.replace(Regex("[^a-z0-9]"), "_")
        return result
    }

    /**
     * Get the cache file for the given URL.
     */
    private fun imageCacheFile(context: Context, url: String) = File(context.cacheDir, muteUrl(url))

    fun isCached(context: Context, url: String): Boolean {
        val file = imageCacheFile(context, url)
        // Check if the file exists
        if (!file.exists()) return false
        // Get the file's last modified time
        val lastModified = file.lastModified()
        // Get the current time
        val currentTime = System.currentTimeMillis()
        // Check if the file is older than the cache duration
        return currentTime - lastModified < cacheDurationMillis
    }

    /**
     * Stores the size of the image in the cache.
     */
    fun store(context: Context, url: String, size: IntSize) {
        val file = imageCacheFile(context, url)

        // Delete the file if it exists
        if (file.exists()) file.delete()

        // Write the size to the file
        file.outputStream().buffered().use { output ->
            output.write(size.width)
            output.write(size.height)
        }
        file.setLastModified(System.currentTimeMillis())
    }

    /**
     * Retrieves the size of the image from the cache.
     */
    fun retrieve(context: Context, url: String): IntSize? {
        val file = imageCacheFile(context, url)
        if (!isCached(context, url)) {
            file.delete() // delete the file just in case it has expired
            return null
        }
        return file.inputStream().buffered().use { input ->
            val width = input.read()
            val height = input.read()
            if (width < 0 || height < 0) null
            else IntSize(width, height)
        }
    }
}
