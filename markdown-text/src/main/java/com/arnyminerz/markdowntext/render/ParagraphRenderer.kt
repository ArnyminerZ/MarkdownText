package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.component.model.TextComponent

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

        val text = buildAnnotatedString(feature.list)

        ClickableText(
            text = text,
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

    @Composable
    override fun append(annotatedStringBuilder: Builder, feature: Paragraph): Builder {
        annotatedStringBuilder.append(firstLinePrefix)
        return appendTextComponents(
            annotatedStringBuilder,
            feature.list,
            object : IRendererAppendCallback() {
                override fun afterEOL(builder: Builder) {
                    builder.append(otherLinesPrefix)
                }
            }
        ).appendLine() as Builder
    }
}
