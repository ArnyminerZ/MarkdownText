package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import com.arnyminerz.markdowntext.component.model.TextComponent

context(RenderContext)
@ExperimentalTextApi
fun IRenderer<*>.appendTextComponents(
    components: List<TextComponent>,
    callback: IRendererAppendCallback = object : IRendererAppendCallback() {}
) {
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

            component is TextComponent.BR -> {
                annotatedStringBuilder.append(component.text)
                callback.afterBR(annotatedStringBuilder)
            }

            TextComponent.Mono.isInstanceOf(component) -> {
                annotatedStringBuilder.append(component.text)
                callback.afterSingleCharacter(annotatedStringBuilder)
            }

            component is TextComponent.Text -> {
                annotatedStringBuilder.append(component.text)
                callback.afterText(annotatedStringBuilder)
            }

            component is TextComponent.CodeSpan -> {
                annotatedStringBuilder.withStyle(textStyles.codeSpanStyle) {
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
                annotatedStringBuilder.withStyle(textStyles.linkSpanStyle) {
                    withAnnotation(
                        UrlAnnotation(component.url)
                    ) {
                        append(component.text)
                    }
                }
            }

            component is TextComponent.Image -> {
                val url = component.url

                // FIXME: Maybe it's not the best place to call this function
                viewModel.obtainImageSize(textSize, url)

                annotatedStringBuilder.appendInlineContent(
                    id = url,
                    alternateText = component.text
                )
            }
        }
    }
}
