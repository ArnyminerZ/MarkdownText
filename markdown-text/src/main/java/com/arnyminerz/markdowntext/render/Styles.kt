package com.arnyminerz.markdowntext.render

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration

object Styles {
    var getCodeSpanStyle: @Composable () -> SpanStyle = {
        SpanStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            background = MaterialTheme.colorScheme.surfaceVariant,
            fontFamily = FontFamily.Monospace
        )
    }

    var getLinkSpanStyle: @Composable () -> SpanStyle = {
        SpanStyle(
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
