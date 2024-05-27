package com.arnyminerz.markdowntext.network

import androidx.compose.ui.unit.IntSize
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("MagicNumber")
object SizeIdentifierPNG : RemoteImageIdentifier {
    /**
     * Check if the input stream is a PNG file.
     * @param input The input stream to check.
     * @return `true` if the input stream is a PNG file, `false` otherwise.
     */
    override fun matches(input: InputStream, reset: Boolean): Boolean {
        val buffer = ByteArray(8)
        input.mark(8)

        // Leer y verificar la firma del archivo PNG (8 bytes)
        input.read(buffer)
        val pngSignature = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        )

        return buffer.contentEquals(pngSignature).also {
            if (reset) input.reset()
        }
    }

    override suspend fun getImageSize(input: InputStream): IntSize  = withContext(Dispatchers.IO) {
        val buffer = ByteArray(8)

        require(matches(input, false)) { "Not a PNG file." }

        // Leer el primer bloque (debería ser IHDR)
        input.read(buffer) // Leer la longitud del bloque IHDR (4 bytes) y el tipo (4 bytes)
        val length = ((buffer[0].toInt() and 0xFF) shl 24) or
            ((buffer[1].toInt() and 0xFF) shl 16) or
            ((buffer[2].toInt() and 0xFF) shl 8) or
            (buffer[3].toInt() and 0xFF)
        val chunkType = String(buffer, 4, 4)

        require(chunkType == "IHDR") { "First chunk is not IHDR." }

        // Leer los datos del bloque IHDR (13 bytes)
        val ihdrData = ByteArray(length)
        input.read(ihdrData)

        // Extraer el ancho y alto de los primeros 8 bytes del bloque IHDR
        val width = ((ihdrData[0].toInt() and 0xFF) shl 24) or
            ((ihdrData[1].toInt() and 0xFF) shl 16) or
            ((ihdrData[2].toInt() and 0xFF) shl 8) or
            (ihdrData[3].toInt() and 0xFF)
        val height = ((ihdrData[4].toInt() and 0xFF) shl 24) or
            ((ihdrData[5].toInt() and 0xFF) shl 16) or
            ((ihdrData[6].toInt() and 0xFF) shl 8) or
            (ihdrData[7].toInt() and 0xFF)

        return@withContext IntSize(width, height)
    }
}