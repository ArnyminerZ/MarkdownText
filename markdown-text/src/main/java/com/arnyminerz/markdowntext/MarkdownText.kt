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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.arnyminerz.markdowntext.annotatedstring.AnnotationStyle
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

private const val TAG = "MarkdownText"

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
 * @param annotationStyle The style to use with the annotated text.
 * @param flavourDescriptor The flavour of Markdown to use.
 * @see GFMFlavourDescriptor
 * @see CommonMarkFlavourDescriptor
 */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Visible,
    maxLines: Int = Int.MAX_VALUE,
    annotationStyle: AnnotationStyle = MarkdownTextDefaults.style,
    flavourDescriptor: MarkdownFlavourDescriptor = CommonMarkFlavourDescriptor()
) {
    val uriHandler = LocalUriHandler.current

    val parsedTree = MarkdownParser(flavourDescriptor).buildMarkdownTreeFromString(markdown)
    val (text, images) = AnnotatedStringGenerator(markdown, parsedTree)
        .generateAnnotatedString(annotationStyle)

    // TODO: Current implementation, since ClickableText is not theming correctly.
    // Reported at https://issuetracker.google.com/issues/255356401
    // Code taken directly from the official sources of ClickableText
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(null) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                val offset = layoutResult.getOffsetForPosition(pos)
                text.getStringAnnotations("link", offset, offset)
                    .firstOrNull()
                    ?.let { stringAnnotation ->
                        try {
                            uriHandler.openUri(stringAnnotation.item)
                        } catch (e: ActivityNotFoundException) {
                            Log.w(TAG, "Could not find link handler.")
                        }
                    }
            }
        }
    }
    val style = LocalTextStyle.current.takeIf { it.fontSize.isSpecified }
        ?: MaterialTheme.typography.bodyMedium

    val inlineContentMap = remember {
        mutableStateMapOf<String, InlineTextContent>().apply {
            putAll(
                images.associate { (url, text) ->
                    url to InlineTextContent(
                        Placeholder(
                            style.fontSize,
                            style.fontSize,
                            PlaceholderVerticalAlign.TextCenter,
                        )
                    ) {
                        Log.d(TAG, "Loading async image for $url...")
                        AsyncImage(
                            model = url,
                            contentDescription = text,
                            modifier = Modifier.fillMaxSize(),
                            onError = {
                                Log.e(TAG, "Could not load image. Error:", it.result.throwable)
                                it.result.throwable.printStackTrace()
                            },
                            onSuccess = {
                                val drawable = it.result.drawable
                                val ratio =
                                    drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()

                                Log.d(TAG, "Image ($url) loaded. Ratio: $ratio")

                                set(url, InlineTextContent(
                                    Placeholder(
                                        (style.fontSize.value * ratio).sp,
                                        style.fontSize,
                                        PlaceholderVerticalAlign.TextCenter,
                                    )
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = text,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                })
                            },
                        )
                    }
                }
            )
        }
    }

    Text(
        text = text,
        modifier = modifier
            .then(pressIndicator),
        onTextLayout = { layoutResult.value = it },
        softWrap = softWrap,
        maxLines = maxLines,
        overflow = overflow,
        inlineContent = inlineContentMap,
    )
}

@Preview
@Composable
fun MarkdownTextPreview() {
    val exampleImageUrl = "https://picsum.photos/300/200"
    val exampleBadge = "https://raster.shields.io/badge/Label-Awesome!-success"
    val exampleLink = "https://example.com"

    val src = listOf(
        "This is markdown text with **bold** content.",
        "This is markdown text with *italic* content.",
        "**This** is where it gets complicated. With **bold and *italic* texts**.",
        "# Headers are also supported",
        "The work for separating sections",
        "## And setting",
        "Sub-sections",
        "with `code` blocks!",
        "### That get",
        "#### Deeper",
        "##### And Deeper",
        "###### And even deeper",
        "[This]($exampleLink) is a link.",
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
        "[ ] First",
        "[ ] Second",
        "[x] Third",
        "[ ] Fifth",
        "--------",
        "That is a hr!",
        "Here is a normal inline image: ![This is an image]($exampleBadge)",
        "But this one has a link: [![This is an image]($exampleBadge)]($exampleBadge)",
        "This is a large block image:",
        "![Large image]($exampleImageUrl)",
    ).joinToString(System.lineSeparator())

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        MarkdownText(
            markdown = src,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
        )
    }
}
