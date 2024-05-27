package com.arnyminerz.markdowntext.network

import androidx.compose.ui.unit.IntSize
import java.io.InputStream
import java.net.URL
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RemoteImageSizeIdentifier {
    /**
     * Get the size of an image from a remote URL.
     * @param url The URL of the image.
     * @return The size of the image.
     * @throws IllegalArgumentException If the file type is not supported. See [SupportedImageFormats]
     */
    suspend fun getImageSize(url: String): IntSize? = withContext(Dispatchers.IO) {
        URL(url).openConnection().getInputStream().buffered().use { input ->
            val format: SupportedImageFormats = identifyFormat(input)
            format.identifier.getImageSize(input)
        }
    }

    enum class SupportedImageFormats(
        val identifier: RemoteImageIdentifier
    ) {
        JPEG(SizeIdentifierJPEG), PNG(SizeIdentifierPNG)
    }

    /**
     * Identify the format of the image.
     * @param input The input stream of the image.
     * @return The format of the image.
     * @throws IllegalArgumentException If the image format is not supported.
     * @see SupportedImageFormats
     */
    @Suppress("UseRequire")
    private fun identifyFormat(input: InputStream): SupportedImageFormats {
        for (format in SupportedImageFormats.entries) {
            if (format.identifier.matches(input)) return format
        }
        throw IllegalArgumentException("Unsupported image format.")
    }
}
