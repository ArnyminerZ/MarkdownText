package com.arnyminerz.markdowntext.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import com.arnyminerz.markdowntext.component.model.TextComponent

@Composable
@ExperimentalTextApi
@Suppress("UnusedReceiverParameter")
fun IRenderer<*>.appendTextComponents(
    annotatedStringBuilder: Builder,
    components: List<TextComponent>,
    callback: IRendererAppendCallback = object : IRendererAppendCallback() {}
): Builder {
    for (component: TextComponent in components) {
        when {
            component is TextComponent.EOL -> {
                annotatedStringBuilder.append(component.text)
                callback.afterEOL(annotatedStringBuilder)
            }

            component is TextComponent.WS -> {
                annotatedStringBuilder.append(component.text)
                callback.afterWS(annotatedStringBuilder)
            }

            TextComponent.SingleCharacterTextObject.isInstanceOf(component) -> {
                annotatedStringBuilder.append(component.text)
                callback.afterSingleCharacter(annotatedStringBuilder)
            }

            component is TextComponent.Text -> {
                annotatedStringBuilder.append(component.text)
                callback.afterText(annotatedStringBuilder)
            }

            component is TextComponent.CodeSpan -> {
                annotatedStringBuilder.withStyle(
                    Styles.getCodeSpanStyle()
                ) {
                    append(component.text)
                }
                callback.afterCodeSpan(annotatedStringBuilder)
            }

            component is TextComponent.StyledText -> {
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
                callback.afterStyledText(annotatedStringBuilder)
            }

            component is TextComponent.Link -> {
                annotatedStringBuilder.withStyle(
                    Styles.getLinkSpanStyle()
                ) {
                    withAnnotation(
                        UrlAnnotation(component.url)
                    ) {
                        append(component.text)
                    }
                }
            }
        }
    }
    return annotatedStringBuilder
}
