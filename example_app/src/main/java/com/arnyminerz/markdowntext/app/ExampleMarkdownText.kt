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
fun MarkdownTextPreview(
    modifier: Modifier = Modifier,
    flavour: MarkdownFlavour = MarkdownFlavour.CommonMark,
) {
    // val exampleImageUrl = "https://picsum.photos/300/200"
    // val exampleBadge = "https://raster.shields.io/badge/Label-Awesome!-success"
    val exampleLink = "https://example.com"

    val src = listOf(
        "# General formatting",
        "This is markdown text with **bold** content.",
        "This is markdown text with *italic* content.",
        "This is markdown text with **bold and *italic* texts**.",
        "This is markdown text with ~~strikethrough~~ content.",
        "Inline `code` annotations",
        "[This]($exampleLink) is a link.",
        "Automatic link: $exampleLink",
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
        "Special characters should be fine: ' \" ! ( ) [ ] ` < >",
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

    MarkdownText(
        markdown = src,
        processor = JetbrainsMarkdownProcessor(flavour),
        modifier = modifier
    )
}
