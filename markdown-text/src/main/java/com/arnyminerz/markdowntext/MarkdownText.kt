package com.arnyminerz.markdowntext

import android.content.ActivityNotFoundException
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

private const val TAG = "MarkdownText"

/**
 * Annotates the [String] using Markdown formatting.
 * @author Arnau Mora
 * @since 20221019
 * @param bodyStyle The default style for the body, and non-annotated texts.
 * @param headlineDepthStyles A list of styles that will be used for headlines. Each element of the
 * list matches the depth given by adding `#`. Example: `###` will use the element at `2` of the list.
 * @param bullet The character to use as bullet for lists.
 * @param linkColor The color to use for tinting links.
 * @return As [AnnotatedString] instance formatted with the given markdown.
 */
@Composable
private fun String.markdownAnnotated(
    bodyStyle: TextStyle = MarkdownTextDefaults.bodyStyle,
    headlineDepthStyles: List<TextStyle> = MarkdownTextDefaults.headlineDepthStyles,
    bullet: Char = MarkdownTextDefaults.bullet,
    linkColor: Color = MarkdownTextDefaults.linkColor,
): Pair<AnnotatedString, Map<String, InlineTextContent>> {
    val images = mutableListOf<Pair<String, String>>()
    var lastStyle = bodyStyle.toSpanStyle()

    val annotatedString = buildAnnotatedString {
        val headlineIndex = indexOf('#')
        if (headlineIndex >= 0) {
            // This is header, count depth
            val regex = Regex("[^#]")
            var depth = 0
            while (!regex.matchesAt(this@markdownAnnotated, depth)) depth++
            val headline = substring(depth + 1)
            val headlineTypography = headlineDepthStyles.getOrElse(depth - 1) { TextStyle.Default }
            withStyle(headlineTypography.toSpanStyle()) { append(headline) }
        } else if (startsWith('-') || startsWith("* ") || startsWith("+ ")) { // List
            val item = substring(1)
            append("$bullet\t$item")
        } else {
            val lineLength = this@markdownAnnotated.length
            var linkStart: Int? = null
            var linkEnd: Int = -1
            lastStyle = bodyStyle.toSpanStyle()
            var c = 0
            pushStyle(bodyStyle.toSpanStyle())
            while (c < lineLength) {
                val char = get(c)
                val nextChar = c.takeIf { it + 1 < lineLength }?.let { get(it + 1) }
                if (char == '\\' && Regex("[*~_!\\[\\]()\\\\]").matches(nextChar.toString())) {
                    append(nextChar?.toString() ?: "\u0000")
                    if (linkStart != null) linkEnd++
                    c += 2
                } else if (char == '*' && nextChar == '*') { // Bold
                    pop()
                    lastStyle = if (lastStyle.fontWeight == FontWeight.Bold)
                        lastStyle.copy(fontWeight = FontWeight.Normal)
                    else
                        lastStyle.copy(fontWeight = FontWeight.Bold)
                    pushStyle(lastStyle)

                    // Add two since the pointer is double
                    c += 2
                } else if (char == '*') { // Italic
                    pop()
                    lastStyle = if (lastStyle.fontStyle == FontStyle.Italic)
                        lastStyle.copy(fontStyle = FontStyle.Normal)
                    else
                        lastStyle.copy(fontStyle = FontStyle.Italic)
                    pushStyle(lastStyle)
                    c++
                } else if (char == '~') { // Strikethrough
                    pop()
                    lastStyle = if (lastStyle.textDecoration == TextDecoration.LineThrough)
                        lastStyle.copy(textDecoration = TextDecoration.None)
                    else
                        lastStyle.copy(textDecoration = TextDecoration.LineThrough)
                    pushStyle(lastStyle)
                    c++
                } else if (char == '_') { // Underline
                    pop()
                    lastStyle = if (lastStyle.textDecoration == TextDecoration.Underline)
                        lastStyle.copy(textDecoration = TextDecoration.None)
                    else
                        lastStyle.copy(textDecoration = TextDecoration.Underline)
                    pushStyle(lastStyle)
                    c++
                } else if (char == '`') { // Code
                    pop()
                    lastStyle = if (lastStyle.fontFeatureSettings == "tnum")
                        lastStyle.copy(fontFeatureSettings = null, fontFamily = FontFamily.Default)
                    else
                        lastStyle.copy(
                            fontFeatureSettings = "tnum",
                            fontFamily = FontFamily.Monospace
                        )
                    pushStyle(lastStyle)
                    c++
                } else if (char == '!') { // Image
                    val openPos = indexOf('[', c + 1)
                    // Search for the closing tag
                    val preClosing = indexOf(']', openPos + 1)
                    // Search for the actual link start
                    val lOpen = indexOf('(', c + 1)
                    // And the ending
                    val lClose = indexOf(')', c + 1)

                    // Check if link is valid
                    val outOfBounds = openPos < 0 || preClosing < 0 || lOpen < 0 || lClose < 0
                    val overwrites = lOpen > lClose || preClosing > lOpen
                    if (outOfBounds || overwrites) {
                        append(char)
                        c++
                    } else {
                        val text = substring(openPos + 1, preClosing)
                        val link = substring(lOpen + 1, lClose)

                        appendInlineContent(id = link, alternateText = text)
                        images.add(link to text)

                        c = lClose + 1
                    }
                } else if (char == '[') { // Starts a link
                    // Search for the closing tag
                    val preClosing = indexOf(']', c + 1)

                    if (preClosing >= 0) {
                        linkStart = c
                        linkEnd = c
                    }
                    c++
                } else if (char == ']' && linkStart != null) { // Ends a link
                    // Search for the actual link start
                    val lOpen = indexOf('(', c + 1)
                    // And the ending
                    val lClose = indexOf(')', c + 1)

                    // Check if link is valid
                    val outOfBounds = lOpen < 0 || lClose < 0
                    val overwrites = lOpen > lClose
                    if (outOfBounds || overwrites) {
                        append(char)
                        c++
                    } else {
                        val link = substring(lOpen + 1, lClose)
                        addStringAnnotation(
                            tag = "link",
                            annotation = link,
                            start = linkStart,
                            end = linkEnd,
                        )
                        addStyle(
                            lastStyle.copy(
                                textDecoration = TextDecoration.Underline,
                                color = linkColor,
                            ),
                            start = linkStart,
                            end = linkEnd,
                        )
                        linkStart = null
                        linkEnd = -1
                        c = lClose + 1
                    }
                } else {
                    append(char)
                    if (linkStart != null)
                        linkEnd++
                    c++
                }
            }
        }
    }
    val inlineContentMap = images.associate { (url, text) ->
        url to InlineTextContent(
            // TODO: Somehow calculate placeholder
            Placeholder(
                lastStyle.fontSize * 2,
                lastStyle.fontSize,
                PlaceholderVerticalAlign.TextCenter
            )
        ) {
            AsyncImage(
                model = url,
                contentDescription = text,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
    return annotatedString to inlineContentMap
}

/**
 * Creates a Text component that supports markdown formatting.
 * @author Arnau Mora
 * @since 20221019
 * @param markdown The markdown-formatted text to display.
 * @param modifier Modifiers to apply to the wrapper.
 * @param softWrap Whether the text should break at soft line breaks. If false, the glyphs in the
 * text will be positioned as if there was unlimited horizontal space. If [softWrap] is `false`,
 * [overflow] and TextAlign may have unexpected effects.
 * @param overflow How visual overflow should be handled.
 * @param maxLines An optional maximum number of lines for the text to span, wrapping if necessary.
 * If the text exceeds the given number of lines, it will be truncated according to [overflow] and
 * [softWrap]. If it is not null, then it must be greater than zero.
 * @param bodyStyle The default style for the body, and non-annotated texts.
 * @param headlineDepthStyles A list of styles that will be used for headlines. Each element of the
 * list matches the depth given by adding `#`. Example: `###` will use the element at `2` of the list.
 * @param bullet The character to use as bullet for lists.
 * @param linkColor The color to use for tinting links.
 */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Visible,
    maxLines: Int = Int.MAX_VALUE,
    bodyStyle: TextStyle = MarkdownTextDefaults.bodyStyle,
    headlineDepthStyles: List<TextStyle> = MarkdownTextDefaults.headlineDepthStyles,
    bullet: Char = MarkdownTextDefaults.bullet,
    linkColor: Color = MarkdownTextDefaults.linkColor,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier,
    ) {
        markdown.split(System.lineSeparator()).forEach { line ->
            if (line.startsWith("--")) // If starts with at least two '-', add divider
                return@forEach Divider()
            if (line.startsWith("!")) { // Image block
                val openPos = line.indexOf('[')
                // Search for the closing tag
                val preClosing = line.indexOf(']', openPos + 1)
                // Search for the actual link start
                val lOpen = line.indexOf('(', preClosing + 1)
                // And the ending
                val lClose = line.indexOf(')', lOpen + 1)

                // Check if link is valid
                val outOfBounds = openPos < 0 || preClosing < 0 || lOpen < 0 || lClose < 0
                val overwrites = lOpen > lClose || preClosing > lOpen
                if (outOfBounds || overwrites) {
                    // Skip image loading
                } else {
                    val text = line.substring(openPos + 1, preClosing)
                    val link = line.substring(lOpen + 1, lClose)

                    AsyncImage(
                        model = link,
                        contentDescription = text,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Inside,
                    )

                    return@forEach
                }
            }

            val (annotatedString, inlineContent) = line.markdownAnnotated(
                bodyStyle, headlineDepthStyles, bullet, linkColor
            )

            // TODO: Current implementation, since ClickableText is not theming correctly.
            // Reported at https://issuetracker.google.com/issues/255356401
            // Code taken directly from the official sources of ClickableText
            val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
            val pressIndicator = Modifier.pointerInput(null) {
                detectTapGestures { pos ->
                    layoutResult.value?.let { layoutResult ->
                        val offset = layoutResult.getOffsetForPosition(pos)
                        annotatedString
                            .getStringAnnotations("link", offset, offset)
                            .firstOrNull()?.let { stringAnnotation ->
                                try {
                                    uriHandler.openUri(stringAnnotation.item)
                                } catch (e: ActivityNotFoundException) {
                                    Log.w(TAG, "Could not find link handler.")
                                }
                            }
                    }
                }
            }

            Text(
                text = annotatedString,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .then(pressIndicator),
                overflow = overflow,
                maxLines = maxLines,
                style = bodyStyle,
                softWrap = softWrap,
                inlineContent = inlineContent,
                onTextLayout = { layoutResult.value = it }
            )
        }
    }
}

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
                "This is markdown text with **bold** content.",
                "This is markdown text with *italic* content.",
                "**This** is where it gets complicated. With **bold and *italic* texts**.",
                "# Headers are also supported",
                "The work for separating sections",
                "Even use \\* backslashed special characters.",
                "## And setting",
                "Sub-sections",
                "with `code` blocks!",
                "### That get",
                "#### Deeper",
                "##### And Deeper",
                "###### And even deeper",
                "Remember _this_ ~not this~? Also works!",
                "[This](https://example.com) is a link.",
                "- Lists",
                "* are",
                "* also",
                "- supported",
                "--------",
                "That is a hr!",
                "Here is a normal inline image: ![This is an image]($exampleBadge)",
                "But this one has a link: [![This is an image]($exampleBadge)]($exampleBadge)",
                "This is a large block image:",
                "![Large image]($exampleImageUrl)",
                "[Links also support \\~ backslashing \\_]($exampleLink)",
            ).joinToString(System.lineSeparator()),
            modifier = Modifier
                .padding(horizontal = 8.dp),
            bodyStyle = MaterialTheme.typography.bodyMedium,
        )
    }
}
