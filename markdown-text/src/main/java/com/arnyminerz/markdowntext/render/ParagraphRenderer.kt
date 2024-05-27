package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.component.model.TextComponent
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

        val inlineContentMap = remember { mutableStateMapOf<String, InlineTextContent>() }
        val text = buildAnnotatedString(inlineContentMap, fontSize, feature.list)

        ExtendedClickableText(
            text = text,
            inlineContent = inlineContentMap,
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
    override fun append(feature: Paragraph) {
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
