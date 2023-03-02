package com.arnyminerz.markdowntext

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import com.arnyminerz.markdowntext.annotatedstring.AnnotationStyle
import org.intellij.markdown.parser.MarkdownParser

@Composable
@ExperimentalTextApi
fun CanvasMarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    flavour: MarkdownFlavour = MarkdownFlavour.Github,
    annotationStyle: AnnotationStyle = MarkdownTextDefaults.style,
) {
    val measurer = rememberTextMeasurer()

    val parsedTree = MarkdownParser(flavour.descriptor).buildMarkdownTreeFromString(markdown)
    val (text, images) = AnnotatedStringGenerator(markdown, parsedTree)
        .generateAnnotatedString(annotationStyle)

    val size = remember { measurer.measure(text).size }

    Box(modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { size.height.toDp() })
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { graphOffset ->
                            // FIXME: Tap position is not detected correctly
                            val measure = measurer.measure(text)
                            val offset = measure.getOffsetForPosition(graphOffset)
                            val annotations = text.getStringAnnotations(offset, offset + 1)
                            Log.i(
                                "MT",
                                "Offset: $offset Annotations (${annotations.size}): $annotations"
                            )
                        },
                    )
                }
        ) {
            drawText(measurer, text)
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Preview
@Composable
fun CanvasMarkdownTextPreview() {
    CanvasMarkdownText(
        markdown = Example.markdownTextExample,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    )
}
