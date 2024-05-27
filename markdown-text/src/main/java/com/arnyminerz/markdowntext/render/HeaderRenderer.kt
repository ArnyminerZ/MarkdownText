package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arnyminerz.markdowntext.MarkdownViewModel
import com.arnyminerz.markdowntext.component.Header
import com.arnyminerz.markdowntext.render.code.LocalCodeParser
import com.arnyminerz.markdowntext.render.code.LocalCodeTheme
import com.arnyminerz.markdowntext.render.style.TextStyles
import com.arnyminerz.markdowntext.ui.ExtendedClickableText
import com.arnyminerz.markdowntext.ui.utils.rememberMaxCharacterSize

@ExperimentalTextApi
object HeaderRenderer : IRenderer<Header> {
    @Composable
    override fun LazyItemScope.Content(feature: Header, modifier: Modifier) {
        val uriHandler = LocalUriHandler.current

        val textStyles = TextStyles.getFromCompositionLocal()
        val codeParser = LocalCodeParser.current
        val codeTheme = LocalCodeTheme.current
        val style = textStyles.headerStyles.fromIndex(feature.depth)
        val textSize = rememberMaxCharacterSize(style = style)

        val viewModel = viewModel<MarkdownViewModel>()
        val text = buildAnnotatedString(textSize, textStyles, codeParser, codeTheme, feature.list, viewModel)

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

    context(RenderContext) override fun append(feature: Header) {
        val style = textStyles.headerStyles.fromIndex(feature.depth)
        annotatedStringBuilder.withStyle(style.toSpanStyle()) {
            appendTextComponents(feature.list)
        }
        annotatedStringBuilder.appendLine()
    }
}
