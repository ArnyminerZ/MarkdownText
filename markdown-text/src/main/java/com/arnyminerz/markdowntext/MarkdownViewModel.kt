package com.arnyminerz.markdowntext

import android.app.Application
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.arnyminerz.markdowntext.network.RemoteImageSizeIdentifier
import com.arnyminerz.markdowntext.network.SizeIdentifierCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class MarkdownViewModel(application: Application) : AndroidViewModel(application) {
    fun obtainImageSize(
        textSize: IntSize,
        url: String,
        inlineContentMap: SnapshotStateMap<String, InlineTextContent>
    ) = viewModelScope.launch {
        // TODO: Handle IllegalArgumentException: URL is not a JPEG or PNG
        val originalImageSize = if (SizeIdentifierCache.isCached(getApplication(), url))
            SizeIdentifierCache.retrieve(getApplication(), url)
        else
            RemoteImageSizeIdentifier.getImageSize(url)

        if (originalImageSize != null) {
            SizeIdentifierCache.store(getApplication(), url, originalImageSize)
        }
        val ratio = originalImageSize?.ratio() ?: 1f

        // Scale imageSize to fit the height of textSize
        val scaledImageSize = originalImageSize?.let {
            IntSize(
                width = (it.width * textSize.height) / it.height,
                height = textSize.height
            )
        }

        val width = scaledImageSize?.width?.sp ?: textSize.width.sp
        val height = scaledImageSize?.height?.sp ?: textSize.height.sp

        inlineContentMap[url] = InlineTextContent(
            Placeholder(
                width = width,
                height = height,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            val lineHeightDp: Dp = with(LocalDensity.current) { textSize.height.toDp() }

            AsyncImage(
                model = url,
                contentDescription = it,
                modifier = Modifier
                    .height(lineHeightDp)
                    .aspectRatio(ratio)
            )
        }
    }
}
