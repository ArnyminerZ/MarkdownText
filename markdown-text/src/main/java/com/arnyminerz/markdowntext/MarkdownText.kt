package com.arnyminerz.markdowntext

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.arnyminerz.markdowntext.component.Header
import com.arnyminerz.markdowntext.component.OrderedList
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.component.UnorderedList
import com.arnyminerz.markdowntext.processor.IProcessor
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import com.arnyminerz.markdowntext.render.HeaderRenderer
import com.arnyminerz.markdowntext.render.ListRenderer
import com.arnyminerz.markdowntext.render.ParagraphRenderer

/**
 * Creates a Text component that supports markdown formatting.
 * @author Arnau Mora
 * @since 20221019
 * @param markdown The markdown-formatted text to display.
 * @see Text
 */
@Composable
fun MarkdownText(
    markdown: String,
    processor: IProcessor,
) {
    val text = processor.load(markdown)

    Text(
        text = buildAnnotatedString {
            for (component in text) {
                when (component) {
                    is Paragraph -> ParagraphRenderer().append(this, component)
                    is OrderedList -> ListRenderer.append(this, component)
                    is UnorderedList -> ListRenderer.append(this, component)
                    is Header -> HeaderRenderer.append(this, component)
                    else -> Log.e("MarkdownText", "Got unknown component: ${component::class.simpleName}")
                }
            }
        }
    )
}

@Preview
@Composable
fun MarkdownTextPreview(
    flavourDescriptor: MarkdownFlavour = MarkdownFlavour.CommonMark,
) {
    // val exampleImageUrl = "https://picsum.photos/300/200"
    // val exampleBadge = "https://raster.shields.io/badge/Label-Awesome!-success"
    // val exampleLink = "https://example.com"

    val src = listOf(
        "# General formatting",
        "This is markdown text with **bold** content.",
        "This is markdown text with *italic* content.",
        "This is markdown text with **bold and *italic* texts**.",
        "This is markdown text with ~~strikethrough~~ content.",
        "Inline `code` annotations",
        // "[This]($exampleLink) is a link.",
        // "Automatic link: $exampleLink",
        "# Header 1",
        "## Header 2",
        "### Header 3",
        "#### Header 4",
        "##### Header 5",
        "###### Header 6",
        "## Unordered lists",
        "- First",
        "- Second",
        "  - Nested item 1",
        "  - Nested item 2",
        "- Third",
        "  with multiline",
        "- Fifth",
        "## Ordered lists",
        "1. First",
        "2. Second",
        "3. Third",
        "4. Fifth",
        // "## Checkboxes",
        // "- [ ] First",
        // "- [ ] Second",
        // "- [x] Third",
        // "- [ ] Fifth",
        // "--------",
        // "/\\ That is a hr! /\\",
        // "# Images",
        // " ![Badge]($exampleBadge)![Badge]($exampleBadge)",
        // "Here is a normal inline image: ![This is an image]($exampleBadge)",
        // "But this one has a link: [![This is an image]($exampleBadge)]($exampleLink)",
        // "This is a large block image:",
        // "![Large image]($exampleImageUrl)",
        // "'Quotes' are rendered \"correctly\" (I hope)",
        // "",
        // "| Column 1  | Column 2 | Column 3 |   |   |",
        // "|-----------|----------|----------|---|---|",
        // "| This      | Is       | A table  |   |   |",
        // "| Formatted | In       | Markdown |   |   |",
        // "|           |          |          |   |   |",
        // "",
        // "```bash",
        // "#!/bin/bash",
        // "echo 'Hello world!'",
        // "```"
    ).joinToString(System.lineSeparator())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        MarkdownText(
            markdown = src,
            processor = JetbrainsMarkdownProcessor()
        )
    }
}
