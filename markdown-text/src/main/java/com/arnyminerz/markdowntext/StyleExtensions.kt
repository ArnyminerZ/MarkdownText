package com.arnyminerz.markdowntext

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

/**
 * Returns a new [SpanStyle] object with the given [FontWeight].
 * @author Arnau Mora
 * @since 20221216
 */
val FontWeight.span: SpanStyle get() = SpanStyle(fontWeight = this)

/**
 * Returns a new [SpanStyle] object with the given [FontStyle].
 * @author Arnau Mora
 * @since 20221216
 */
val FontStyle.span: SpanStyle get() = SpanStyle(fontStyle = this)

/**
 * Returns a new [SpanStyle] object with the given [TextDecoration].
 * @author Arnau Mora
 * @since 20221216
 */
val TextDecoration.span: SpanStyle get() = SpanStyle(textDecoration = this)
