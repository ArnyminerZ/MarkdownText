package com.arnyminerz.markdowntext.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.tooling.preview.Preview
import com.arnyminerz.markdowntext.MarkdownContainer
import com.arnyminerz.markdowntext.MarkdownFlavour
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor

@Preview
@Composable
@OptIn(ExperimentalTextApi::class)
fun ExampleMarkdownContainer(
    modifier: Modifier = Modifier,
    flavour: MarkdownFlavour = MarkdownFlavour.CommonMark,
) {
    MarkdownContainer(
        markdown = exampleMarkdown,
        processor = JetbrainsMarkdownProcessor(flavour),
        modifier = modifier
    )
}
