package com.arnyminerz.markdowntext.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.arnyminerz.markdowntext.component.model.TextComponent

@Composable
fun IRenderer<*>.appendTextComponents(
    annotatedStringBuilder: Builder,
    components: List<TextComponent>,
    afterEOL: Builder.() -> Unit = {},
    afterWS: Builder.() -> Unit = {},
    afterText: Builder.() -> Unit = {},
    afterCodeSpan: Builder.() -> Unit = {},
    afterStyledText: Builder.() -> Unit = {}
): Builder {
    for (component: TextComponent in components) {
        when (component) {
            is TextComponent.EOL -> {
                annotatedStringBuilder.appendLine()
                afterEOL(annotatedStringBuilder)
            }

            is TextComponent.WS -> {
                annotatedStringBuilder.append(' ')
                afterWS(annotatedStringBuilder)
            }
            is TextComponent.Text -> {
                annotatedStringBuilder.append(component.text)
                afterText(annotatedStringBuilder)
            }
            is TextComponent.CodeSpan -> {
                annotatedStringBuilder.withStyle(
                    Styles.getCodeSpanStyle()
                ) {
                    append(component.text)
                }
                afterCodeSpan(annotatedStringBuilder)
            }
            is TextComponent.StyledText -> {
                annotatedStringBuilder.withStyle(
                    SpanStyle(
                        fontWeight = if (component.isBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (component.isItalic) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = if (component.isStrikethrough)
                            TextDecoration.LineThrough
                        else
                            TextDecoration.None
                    )
                ) {
                    append(component.text)
                }
                afterStyledText(annotatedStringBuilder)
            }
        }
    }
    return annotatedStringBuilder
}
