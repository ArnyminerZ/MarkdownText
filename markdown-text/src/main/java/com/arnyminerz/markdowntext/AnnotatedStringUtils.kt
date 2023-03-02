package com.arnyminerz.markdowntext

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.withAnnotation

fun AnnotatedString.Builder.append(text: CharSequence) = append(text.toString())

fun AnnotatedString.Builder.appendInlineContent(
    id: CharSequence,
    alternateText: CharSequence
) = appendInlineContent(id.toString(), alternateText.toString())

@ExperimentalTextApi
inline fun <R : Any> AnnotatedString.Builder.withAnnotation(
    tag: CharSequence,
    annotation: CharSequence,
    crossinline block: AnnotatedString.Builder.() -> R
): R = withAnnotation(tag.toString(), annotation.toString(), block)
