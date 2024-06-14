package com.arnyminerz.markdowntext.render.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

data class HeaderStyles(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val h5: TextStyle,
    val h6: TextStyle
) {
    companion object {
        @Composable
        fun fromMaterialTheme(): HeaderStyles {
            return HeaderStyles(
                h1 = MaterialTheme.typography.headlineLarge,
                h2 = MaterialTheme.typography.headlineMedium,
                h3 = MaterialTheme.typography.headlineSmall,
                h4 = MaterialTheme.typography.titleLarge,
                h5 = MaterialTheme.typography.titleMedium,
                h6 = MaterialTheme.typography.titleSmall
            )
        }
    }

    fun fromIndex(index: Int): TextStyle = @Suppress("MagicNumber") when (index) {
        0 -> h1
        1 -> h2
        2 -> h3
        3 -> h4
        4 -> h5
        5 -> h6
        else -> error("Got a depth out of bounds: $index")
    }
}
