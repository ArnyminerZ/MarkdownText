package com.arnyminerz.markdowntext.render

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily

object Styles {
    var getCodeSpanStyle: @Composable () -> SpanStyle = {
        SpanStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            background = MaterialTheme.colorScheme.surfaceVariant,
            fontFamily = FontFamily.Monospace
        )
    }
}
