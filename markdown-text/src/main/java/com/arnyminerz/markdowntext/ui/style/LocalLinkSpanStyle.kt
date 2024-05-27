package com.arnyminerz.markdowntext.ui.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration

val LocalLinkSpanStyle = compositionLocalOf<SpanStyle?> { null }

@Composable
fun defaultLinkSpanStyle(): SpanStyle {
    return SpanStyle(
        textDecoration = TextDecoration.Underline,
        color = MaterialTheme.colorScheme.primary
    )
}
