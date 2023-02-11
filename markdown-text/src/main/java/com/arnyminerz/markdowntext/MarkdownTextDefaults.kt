package com.arnyminerz.markdowntext

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import com.arnyminerz.markdowntext.annotatedstring.AnnotationStyle

/**
 * Provides the default values for calling [MarkdownText].
 * @author Arnau Mora
 * @since 20221019
 */
object MarkdownTextDefaults {
    val headlineDepthStyles
        @Composable
        get() = listOf(
            MaterialTheme.typography.headlineLarge,
            MaterialTheme.typography.headlineMedium,
            MaterialTheme.typography.headlineSmall,
            MaterialTheme.typography.titleLarge,
            MaterialTheme.typography.titleMedium,
            MaterialTheme.typography.titleSmall,
        )

    val codeBlockStyle: SpanStyle
        @Composable
        get() = SpanStyle(
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            background = MaterialTheme.colorScheme.tertiaryContainer,
            fontFamily = FontFamily.Monospace,
        )

    val linkStyle: SpanStyle
        @Composable
        get() = SpanStyle(
            textDecoration = TextDecoration.Underline,
            color = linkColor,
        )

    val style: AnnotationStyle
        @Composable
        get() = AnnotationStyle(headlineDepthStyles, codeBlockStyle, linkStyle, bullet)

    /**
     * The color given to links in [MarkdownText].
     * @author Arnau Mora
     * @since 20221019
     */
    val linkColor = Color(0xff64B5F6)

    /**
     * The character used by [MarkdownText] to mark list items.
     * @author Arnau Mora
     * @since 20221019
     */
    const val bullet = '\u2022'
}
