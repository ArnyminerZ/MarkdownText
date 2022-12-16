package com.arnyminerz.markdowntext.annotatedstring

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle

fun AnnotatedString.Builder.append(charSequence: CharSequence) = append(charSequence.toString())

context(AnnotationStyle)
        @ExperimentalTextApi
fun AnnotatedString.Builder.appendLink(
    url: CharSequence,
    text: CharSequence = url,
): Unit = withStyle(this@AnnotationStyle.linkStyle) {
    withAnnotation(
        tag = "link",
        annotation = url.toString(),
    ) { append(text) }
}
