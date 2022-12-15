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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.arnyminerz.markdowntext.annotatedstring.AnnotationStyle
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
 * @param flavour The flavour of Markdown to use.
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
    flavour: MarkdownFlavour = MarkdownFlavour.CommonMark
) {
    val uriHandler = LocalUriHandler.current
    val density = LocalDensity.current

    val parsedTree = MarkdownParser(flavour.descriptor).buildMarkdownTreeFromString(markdown)
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
    var size: IntSize = remember { IntSize.Zero }

    val inlineContentMap = remember {
        mutableStateMapOf<String, InlineTextContent>().apply {
            putAll(
                images.associate { (url, text, fullWidth) ->
                    val fontSize = style.fontSize
                    url to InlineTextContent(
                        Placeholder(
                            fontSize,
                            fontSize,
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
                                val whRatio =
                                    drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
                                val hwRatio =
                                    drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth.toFloat()

                                Log.d(
                                    TAG,
                                    "Image ($url) loaded. Ratio: $whRatio. fullWidth=$fullWidth. size=$size)"
                                )

                                val width = with(density) { size.width.toSp() }.takeIf { fullWidth }
                                    ?: (fontSize.value * whRatio).sp
                                val height =
                                    with(density) { (size.width * hwRatio).toSp() }.takeIf { fullWidth }
                                        ?: fontSize
                                set(
                                    url, InlineTextContent(
                                        Placeholder(
                                            width,
                                            height,
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
            .onGloballyPositioned { size = it.size }
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
fun MarkdownTextPreview(
    flavourDescriptor: MarkdownFlavour = MarkdownFlavour.CommonMark,
) {
    val exampleImageUrl = "https://picsum.photos/300/200"
    val exampleBadge = "https://raster.shields.io/badge/Label-Awesome!-success"
    val exampleLink = "https://example.com"

    val src = listOf(
        "# General formatting",
        "This is markdown text with **bold** content.",
        "This is markdown text with *italic* content.",
        "This is markdown text with **bold and *italic* texts**.",
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
        "[ ] First",
        "[ ] Second",
        "[x] Third",
        "[ ] Fifth",
        "--------",
        "/\\ That is a hr! /\\",
        "# Images",
        " ![Badge]($exampleBadge)![Badge]($exampleBadge)",
        "Here is a normal inline image: ![This is an image]($exampleBadge)",
        "But this one has a link: [![This is an image]($exampleBadge)]($exampleLink)",
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
            flavour = flavourDescriptor,
        )
    }
}
