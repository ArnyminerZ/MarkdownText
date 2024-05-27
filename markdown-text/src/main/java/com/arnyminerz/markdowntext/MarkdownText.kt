package com.arnyminerz.markdowntext

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import com.arnyminerz.markdowntext.processor.IProcessor
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import com.arnyminerz.markdowntext.render.buildAnnotatedString
import com.arnyminerz.markdowntext.ui.ExtendedClickableText
import com.arnyminerz.markdowntext.ui.utils.rememberMaxCharacterSize

/**
 * Creates a Text component that supports markdown formatting.
 * @author Arnau Mora
 * @since 20221019
 * @param markdown The markdown-formatted text to display.
 * @see Text
 */
@Composable
@ExperimentalTextApi
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    processor: IProcessor = JetbrainsMarkdownProcessor(),
) {
    val uriHandler = LocalUriHandler.current

    val fontSize = rememberMaxCharacterSize(style)

    val components = remember(markdown) { processor.load(markdown) }
    val inlineContentMap = remember { mutableStateMapOf<String, InlineTextContent>() }
    val text = buildAnnotatedString(inlineContentMap, fontSize, components)

    ExtendedClickableText(
        text = text,
        inlineContent = inlineContentMap,
        onClick = { index ->
            // Launch the first tapped url annotation, if any
            text.getUrlAnnotations(index, index)
                .firstOrNull()
                ?.let { uriHandler.openUri(it.item.url) }
        },
        style = style,
        modifier = modifier
    )
}
