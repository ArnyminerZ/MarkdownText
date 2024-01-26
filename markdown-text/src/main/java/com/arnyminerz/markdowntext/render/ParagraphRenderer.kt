package com.arnyminerz.markdowntext.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
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
class ParagraphRenderer(
    private val firstLinePrefix: String = "",
    private val otherLinesPrefix: String = ""
) : IRenderer<Paragraph> {
    @Composable
    override fun Content(feature: Paragraph) {
        TODO("Not yet implemented")
    }

    override fun append(annotatedStringBuilder: AnnotatedString.Builder, feature: Paragraph) {
        annotatedStringBuilder.append(firstLinePrefix)
        for (component: TextComponent in feature.list) {
            when (component) {
                is TextComponent.EOL -> {
                    annotatedStringBuilder.appendLine()
                    annotatedStringBuilder.append(otherLinesPrefix)
                }

                is TextComponent.WS -> annotatedStringBuilder.append(' ')
                is TextComponent.Text -> annotatedStringBuilder.append(component.text)
                is TextComponent.StyledText -> annotatedStringBuilder.withStyle(
                    SpanStyle(
                        fontWeight = if (component.isBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (component.isItalic) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = if (component.isStrikethrough) TextDecoration.LineThrough else TextDecoration.None
                    )
                ) {
                    append(component.text)
                }
            }
        }
    }
}
