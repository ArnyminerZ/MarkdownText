package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
import com.arnyminerz.markdowntext.component.Header

@ExperimentalTextApi
object HeaderRenderer : IRenderer<Header> {
    var getHeaderStyle: @Composable (depth: Int) -> TextStyle = {
        when (it) {
            0 -> MaterialTheme.typography.headlineLarge
            1 -> MaterialTheme.typography.headlineMedium
            2 -> MaterialTheme.typography.headlineSmall
            3 -> MaterialTheme.typography.titleLarge
            4 -> MaterialTheme.typography.titleMedium
            5 -> MaterialTheme.typography.titleSmall
            else -> error("Got a depth out of bounds: $it")
        }
    }

    @Composable
    override fun LazyItemScope.Content(feature: Header, modifier: Modifier) {
        val uriHandler = LocalUriHandler.current

        val text = buildAnnotatedString(feature.list)

        ClickableText(
            text = text,
            onClick = { index ->
                // Launch the first tapped url annotation, if any
                text.getUrlAnnotations(index, index)
                    .firstOrNull()
                    ?.let { uriHandler.openUri(it.item.url) }
            },
            style = getHeaderStyle(feature.depth),
            modifier = modifier
        )
    }

    @Composable
    override fun append(annotatedStringBuilder: Builder, feature: Header): Builder {
        val style = getHeaderStyle(feature.depth)
        annotatedStringBuilder.withStyle(style.toSpanStyle()) {
            appendTextComponents(annotatedStringBuilder, feature.list)
        }
        annotatedStringBuilder.appendLine()
        return annotatedStringBuilder
    }
}
