package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arnyminerz.markdowntext.MarkdownViewModel
import com.arnyminerz.markdowntext.component.Header
import com.arnyminerz.markdowntext.ui.ExtendedClickableText
import com.arnyminerz.markdowntext.ui.utils.rememberMaxCharacterSize

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

        val style = getHeaderStyle(feature.depth)
        val textSize = rememberMaxCharacterSize(style = style)

        val viewModel = viewModel<MarkdownViewModel>()
        val text = buildAnnotatedString(textSize, feature.list, viewModel)

        ExtendedClickableText(
            text = text,
            inlineContent = viewModel.inlineContentMap,
            onClick = { index ->
                // Launch the first tapped url annotation, if any
                text.getUrlAnnotations(index, index)
                    .firstOrNull()
                    ?.let { uriHandler.openUri(it.item.url) }
            },
            style = style,
            modifier = modifier
        )
    }

    context(RenderContext)
    @Composable
    override fun append(
        feature: Header
    ) {
        val style = getHeaderStyle(feature.depth)
        annotatedStringBuilder.withStyle(style.toSpanStyle()) {
            appendTextComponents(feature.list)
        }
        annotatedStringBuilder.appendLine()
    }
}
