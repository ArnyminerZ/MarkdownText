package com.arnyminerz.markdowntext.render

import androidx.compose.runtime.Composable
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
 * @param firstLinePrefix Will be added before the first line of the paragraph.
 * @param otherLinesPrefix Will be added before each line of the paragraph starting from the second.
 */
@ExperimentalTextApi
class ParagraphRenderer(
    private val firstLinePrefix: String = "",
    private val otherLinesPrefix: String = ""
) : IRenderer<Paragraph> {
    @Composable
    override fun Content(feature: Paragraph) {
        TODO("Not yet implemented")
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
