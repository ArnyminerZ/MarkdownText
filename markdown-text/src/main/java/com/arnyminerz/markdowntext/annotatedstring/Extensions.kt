package com.arnyminerz.markdowntext.annotatedstring

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.*
import androidx.compose.ui.text.AnnotatedString.Builder

fun Builder.append(charSequence: CharSequence) = append(charSequence.toString())

fun Builder.appendInlineContent(id: CharSequence, alternateText: CharSequence) =
    appendInlineContent(id.toString(), alternateText.toString())

context(AnnotationStyle)
        @ExperimentalTextApi
fun Builder.appendLink(
    url: CharSequence,
    text: CharSequence = url,
): Unit = withStyle(this@AnnotationStyle.linkStyle) {
    withAnnotation(
        tag = "link",
        annotation = url.toString(),
    ) { append(text) }
}

context(AnnotationStyle)
        @ExperimentalTextApi
fun Builder.appendInlineContentLink(
    url: CharSequence,
    imageUrl: CharSequence,
    text: CharSequence = url,
): Unit = withAnnotation(
    tag = "link",
    annotation = url.toString(),
) { appendInlineContent(imageUrl, text) }

context(Builder)
fun <R : Any> SpanStyle.style(block: Builder.() -> R): R =
    this@Builder.withStyle<R>(this) { block() }
