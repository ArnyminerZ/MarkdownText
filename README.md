# MarkdownText

A library for displaying Markdown contents within Jetpack Compose. Uses [Coil][coil-url]

[![Latest version][version-badge]][maven-central-search-url]

## Current limitations

* If a line starts with an image, it will fill the whole width. To fix this, add an space before the
  image:

```markdown
 ![example-image](https://example.com)
```

* Placeholders are not supported.
* Blockquotes are not supported.
* Nested lists are not supported.
* Code blocks are not supported.
* Tables are not supported.
* Horizontal rules do not fill the whole width.

## Usage

Add to the module's dependencies:

```kotlin
implementation('com.arnyminerz.markdowntext:markdowntext:2.0.0')
```

Jetpack Compose example:

```kotlin
@Preview
@Composable
fun MarkdownTextPreview() {
    val exampleImageUrl = "https://picsum.photos/300/200"
    val exampleBadge = "https://raster.shields.io/badge/Label-Awesome!-success"
    val exampleLink = "https://example.com"

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        MarkdownText(
            markdown = listOf(
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
                "* Second",
                "* Third",
                "- Fifth",
                "## Ordered lists",
                "1. First",
                "2. Second",
                "3. Third",
                "4. Fifth",
                "## Checkboxes",
                "- [ ] First",
                "- [ ] Second",
                "- [x] Third",
                "- [ ] Fifth",
                "--------",
                "/\\ That is a hr! /\\",
                "# Images",
                " ![Badge]($exampleBadge)![Badge]($exampleBadge)",
                "Here is a normal inline image: ![This is an image]($exampleBadge)",
                "But this one has a link: [![This is an image]($exampleBadge)]($exampleLink)",
                "This is a large block image:",
                "![Large image]($exampleImageUrl)",
            ).joinToString(System.lineSeparator()),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
        )
    }
}
```

<div width="100%">
  <img width="30%" src="/docs/screenshot1.png" alt="Sample Screenshot 1" />
  <img width="30%" src="/docs/screenshot2.png" alt="Sample Screenshot 2" />
</div>

# Flavours

There are different flavours for different specifications of Markdown. The flavour to use can be set
with the `flavour` parameter of `MarkdownText`. The available flavours and their exclusive features
are:

* `MarkdownFlavour.CommonMark`
* `MarkdownFlavour.Github`
    * Checkboxes
    * Strikethrough
    * Tables (not supported)
    * Automatic links

# Click listener

By default, `MarkdownText` handles all links embed in the markdown text given, and will launch them
using the default browser. However, you can pass the optional parameter `onClick`, and an additional
click listener will be added. Summary:

1. If `onClick` is `null`:
    * Clicking on links will launch them with the browser.
2. If `onClick` is not `null`:
    * If `onClickOverrides` is `false`:
        * Tapping a link will launch it.
        * Tapping something else will call `onClick`
    * If `onClickOverrides` is `true`:
        * Tapping anything will call `onClick`

# Set log level

If you want to see the logs of the library, you can set the log level with `Logger`:
```kotlin
import com.arnyminerz.markdowntext.Logger
import com.arnyminerz.markdowntext.Logger.Level

Logger.setLogLevel(Level.DEBUG)
```

> ![INFO] The default log level is `Level.INFO`.

# Used by

Feel free to open a new PR to add your application or implementation here:

* jtx Board ([Github](https://github.com/TechbeeAT/jtxBoard/))

  [<img alt="Download the app from Google Play Store" height="70px" src="/docs/google-play-badge.png" title="Google Play Badge"/>](https://play.google.com/store/apps/details?id=at.techbee.jtx)
  [<img alt="Download the app from F-Droid" height="70px" src="/docs/fdroid-badge.svg" title="F-Droid Badge"/>](https://f-droid.org/de/packages/at.techbee.jtx/)

[coil-url]: https://coil-kt.github.io/coil

[example-image1]: /docs/screenshot1.png

[example-image2]: /docs/screenshot2.png

[version-badge]: https://img.shields.io/maven-central/v/com.arnyminerz.markdowntext/markdowntext?style=for-the-badge

[maven-central-search-url]: https://search.maven.org/search?q=a:markdowntext

[google-play-badge]: /docs/google-play-badge.png
