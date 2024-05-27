package com.arnyminerz.markdowntext.network

import androidx.compose.ui.unit.IntSize
import java.io.InputStream

interface RemoteImageIdentifier {
    fun matches(input: InputStream, reset: Boolean = true): Boolean

    suspend fun getImageSize(input: InputStream): IntSize?
}
