package com.arnyminerz.markdowntext.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.tooling.preview.Preview
import com.arnyminerz.markdowntext.MarkdownFlavour
import com.arnyminerz.markdowntext.MarkdownText
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor

@Preview
@Composable
@OptIn(ExperimentalTextApi::class)
fun ExampleMarkdownText(
    modifier: Modifier = Modifier,
    flavour: MarkdownFlavour = MarkdownFlavour.CommonMark,
) {
    MarkdownText(
        markdown = exampleMarkdown,
        processor = JetbrainsMarkdownProcessor(flavour),
        modifier = modifier
    )
}
