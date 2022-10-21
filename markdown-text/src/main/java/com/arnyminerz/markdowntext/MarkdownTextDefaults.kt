package com.arnyminerz.markdowntext

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * Provides the default values for calling [MarkdownText] and [markdownAnnotated].
 * @author Arnau Mora
 * @since 20221019
 */
object MarkdownTextDefaults {
    val bodyStyle: TextStyle
        @Composable
        get() = MaterialTheme.typography.bodyMedium

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
