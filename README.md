# MarkdownText

A library for displaying Markdown contents within Jetpack Compose. Uses [Coil][coil-url]

[![Latest version][version-badge]][[maven-central-search-url]]

## Current limitations

* Lists that are annotated with the `*` must have an space after the delimiter.
* If a line starts with an image, just the first one will be loaded. eg:

```markdown
![example-image](https://example.com)![example-image-2](https://example.com)
```

Will only display the first image.

* Placeholders are not supported.
* Header closing tags are not supported.
* Blockquotes are not supported.
* List elements must be in the same line.
* Code blocks are not supported.
* Horizontal rules are only supported for `-`, starting with at least 2 characters without spaces.
* Automatic links are not supported.
* Backslash of special characters is not supported.

## Usage

Add to the module's dependencies:

```groovy
implementation 'com.arnyminerz.markdowntext:markdowntext:1.0.1'
```

Jetpack Compose example:

```kotlin
@Composable
fun MarkdownTextPreview() {
    val exampleImageUrl = "https://picsum.photos/300/200"
    val exampleBadge = "https://raster.shields.io/badge/Label-Awesome!-success"

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        MarkdownText(
            markdown = listOf(
                "This is markdown text with **bold** content.",
                "This is markdown text with *italic* content.",
                "**This** is where it gets complicated. With **bold and *italic* texts**.",
                "# Headers are also supported",
                "The work for separating sections",
                "## And setting",
                "Sub-sections",
                "### That get",
                "#### Deeper",
                "##### And Deeper",
                "###### And even deeper",
                "Remember _this_ ~not this~? Also works!",
                "[This](https://example.com) is a link.",
                "- Lists",
                "- are",
                "- also",
                "- supported",
                "--------",
                "That is a hr!",
                "Here is a normal inline image: ![This is an image]($exampleBadge)",
                "But this one has a link: [![This is an image]($exampleBadge)]($exampleBadge)",
                "This is a large block image:",
                "![Large image]($exampleImageUrl)",
            ).joinToString(System.lineSeparator()),
            modifier = Modifier
                .padding(horizontal = 8.dp),
            bodyStyle = MaterialTheme.typography.bodyMedium,
        )
    }
}
```

![example-image][example-image-url]

[coil-url]: https://coil-kt.github.io/coil

[example-image-url]: /docs/screenshot.png

[version-badge]: https://img.shields.io/maven-central/v/com.arnyminerz.markdowntext/markdowntext?style=for-the-badge

[maven-central-search-url]: https://search.maven.org/search?q=a:markdowntext
