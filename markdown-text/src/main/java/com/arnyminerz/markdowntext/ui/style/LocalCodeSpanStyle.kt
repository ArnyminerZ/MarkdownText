package com.arnyminerz.markdowntext.ui.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily

val LocalCodeSpanStyle = compositionLocalOf<SpanStyle?> { null }

@Composable
fun defaultCodeSpanStyle(): SpanStyle {
    return SpanStyle(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        background = MaterialTheme.colorScheme.surfaceVariant,
        fontFamily = FontFamily.Monospace
    )
}
