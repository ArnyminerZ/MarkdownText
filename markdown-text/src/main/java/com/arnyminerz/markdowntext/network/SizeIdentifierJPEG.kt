package com.arnyminerz.markdowntext.network

import androidx.compose.ui.unit.IntSize
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("MagicNumber")
object SizeIdentifierJPEG : RemoteImageIdentifier {
    private const val JPEG_SOI_1 = 0xFF
    private const val JPEG_SOI_2 = 0xD8

    private const val JPEG_SOF_START = 0xC0
    private const val JPEG_SOF_END = 0xC3
    
    /**
     * Check if the input stream is a JPEG file.
     * @param input The input stream to check.
     * @return `true` if the input stream is a JPEG file, `false` otherwise.
     */
    override fun matches(input: InputStream, reset: Boolean): Boolean {
        val buffer = ByteArray(2)
        input.mark(2)

        // JPEG only requires the first two bytes to be the SOI marker
        input.read(buffer)

        return (buffer[0] == JPEG_SOI_1.toByte() && buffer[1] == JPEG_SOI_2.toByte()).also {
            if (reset) input.reset()
        }
    }

    override suspend fun getImageSize(input: InputStream): IntSize? = withContext(Dispatchers.IO) {
        val buffer = ByteArray(2)

        // Make sure it's a JPEG file
        require(matches(input, false)) { "Not a JPEG file." }

        while (input.read(buffer) != -1) {
            if (buffer[0] == 0xFF.toByte()) {
                val marker = buffer[1].toInt() and 0xFF

                // Check if it's a Start Of Frame (SOF) block
                if (marker in JPEG_SOF_START..JPEG_SOF_END) {
                    input.read(buffer) // read SOF block length (2 bytes)
                    val length =
                        ((buffer[0].toInt() and 0xFF) shl 8) or (buffer[1].toInt() and 0xFF)
                    val sofData = ByteArray(length - 2)
                    input.read(sofData)

                    // Height and width are in bytes 3-6 of the SOF block
                    val height =
                        ((sofData[1].toInt() and 0xFF) shl 8) or (sofData[2].toInt() and 0xFF)
                    val width =
                        ((sofData[3].toInt() and 0xFF) shl 8) or (sofData[4].toInt() and 0xFF)

                    return@withContext IntSize(width, height)
                } else {
                    // Skip other blocks
                    input.read(buffer)
                    val length =
                        ((buffer[0].toInt() and 0xFF) shl 8) or (buffer[1].toInt() and 0xFF)
                    input.skip(length - 2L)
                }
            }
        }
        null
    }
}
