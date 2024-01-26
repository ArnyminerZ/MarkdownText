package com.arnyminerz.markdowntext.render

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import com.arnyminerz.markdowntext.component.Header

object HeaderRenderer : IRenderer<Header> {
    var getHeaderStyle: @Composable (depth: Int) -> SpanStyle = {
        when (it) {
            0 -> MaterialTheme.typography.headlineLarge
            1 -> MaterialTheme.typography.headlineMedium
            2 -> MaterialTheme.typography.headlineSmall
            3 -> MaterialTheme.typography.titleLarge
            4 -> MaterialTheme.typography.titleMedium
            5 -> MaterialTheme.typography.titleSmall
            else -> error("Got a depth out of bounds: $it")
        }.toSpanStyle()
    }

    @Composable
    override fun Content(feature: Header) {
        TODO("Not yet implemented")
    }

    @Composable
    override fun append(annotatedStringBuilder: Builder, feature: Header): Builder {
        val style = getHeaderStyle(feature.depth)
        annotatedStringBuilder.withStyle(style) {
            appendTextComponents(annotatedStringBuilder, feature.list)
        }
        annotatedStringBuilder.appendLine()
        return annotatedStringBuilder
    }
}
