package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arnyminerz.markdowntext.MarkdownViewModel
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.render.code.LocalCodeParser
import com.arnyminerz.markdowntext.render.code.LocalCodeTheme
import com.arnyminerz.markdowntext.render.style.TextStyles
import com.arnyminerz.markdowntext.ui.ExtendedClickableText
import com.arnyminerz.markdowntext.ui.utils.rememberMaxCharacterSize

/**
 * Allows rendering [Paragraph] components.
 *
 * Uses [LocalUriHandler] for launching URLs.
 * @param firstLinePrefix Will be added before the first line of the paragraph.
 * @param otherLinesPrefix Will be added before each line of the paragraph starting from the second.
 */
@ExperimentalTextApi
class ParagraphRenderer(
    private val firstLinePrefix: String = "",
    private val otherLinesPrefix: String = ""
) : IRenderer<Paragraph> {
    @Composable
    override fun LazyItemScope.Content(feature: Paragraph, modifier: Modifier) {
        val uriHandler = LocalUriHandler.current
        val style = LocalTextStyle.current

        val fontSize = rememberMaxCharacterSize(style)
        val codeParser = LocalCodeParser.current
        val codeTheme = LocalCodeTheme.current
        val textStyles = TextStyles.getFromCompositionLocal()

        val viewModel = viewModel<MarkdownViewModel>()
        val text = buildAnnotatedString(fontSize, textStyles, codeParser, codeTheme, feature.list, viewModel)

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

    context(RenderContext) override fun append(feature: Paragraph) {
        annotatedStringBuilder.append(firstLinePrefix)
        appendTextComponents(
            feature.list,
            object : IRendererAppendCallback() {
                override fun afterEOL(builder: Builder) {
                    builder.append(otherLinesPrefix)
                }
            }
        )
        annotatedStringBuilder.appendLine()
    }
}
