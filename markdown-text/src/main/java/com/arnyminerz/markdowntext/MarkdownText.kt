package com.arnyminerz.markdowntext

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arnyminerz.markdowntext.processor.IProcessor
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import com.arnyminerz.markdowntext.render.code.LocalCodeParser
import com.arnyminerz.markdowntext.render.code.LocalCodeTheme
import com.arnyminerz.markdowntext.render.style.TextStyles
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
    placeholder: @Composable () -> Unit = {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
) {
    val uriHandler = LocalUriHandler.current

    val textStyles = TextStyles.getFromCompositionLocal()
    val codeParser = LocalCodeParser.current
    val codeTheme = LocalCodeTheme.current

    val viewModel = viewModel<MarkdownViewModel>()

    val fontSize = rememberMaxCharacterSize(style)

    val annotatedString by remember(markdown, processor) {
        viewModel.processMarkdown(markdown, fontSize, textStyles, codeParser, codeTheme, processor)
    }.collectAsState(initial = null)

    annotatedString?.let { text ->
        ExtendedClickableText(
            text = text,
            inlineContent = viewModel.inlineContentMap,
            onClick = { index ->
                // Launch the first tapped url annotation, if any
                text.getUrlAnnotations(index, index)
                    .firstOrNull()
                    ?.let { uriHandler.openUri(it.item.url) }
            },
            style = style,
            modifier = modifier
        )
    } ?: placeholder()
}
