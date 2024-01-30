package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import com.arnyminerz.markdowntext.component.model.IListComponent
import com.arnyminerz.markdowntext.component.model.ListElement

@ExperimentalTextApi
object ListRenderer : IRenderer<IListComponent> {
    /**
     * This is the algorithm that calculates the prefix to add to each list item for padding from
     * its depth.
     * Default:
     * ```kotlin
     * "  ".repeat(depth + 1)
     * ```
     */
    var paddingAlgorithm: (depth: Int) -> String = { "  ".repeat(it + 1) }

    @Composable
    override fun Content(feature: IListComponent, modifier: Modifier) {
        val uriHandler = LocalUriHandler.current
        val style = LocalTextStyle.current

        val text = buildAnnotatedString(feature.list)

        ClickableText(
            text = text,
            onClick = { index ->
                // Launch the first tapped url annotation, if any
                text.getUrlAnnotations(index, index)
                    .firstOrNull()
                    ?.let { uriHandler.openUri(it.item.url) }
            },
            style = style,
            modifier = modifier
        )
    }

    @Composable
    private fun append(
        annotatedStringBuilder: AnnotatedString.Builder,
        elements: List<ListElement>,
        depth: Int = 0
    ): AnnotatedString.Builder {
        val pad = paddingAlgorithm(depth)
        for (element in elements) {
            // Add the pad
            annotatedStringBuilder.append(pad)
            // Append the prefix string
            annotatedStringBuilder.append(element.prefix)
            // Add a tab for giving a bit of space
            annotatedStringBuilder.append('\t')
            // Render the paragraph
            ParagraphRenderer(otherLinesPrefix = "$pad \t")
                .append(annotatedStringBuilder, element.paragraph)

            element.subList?.let { append(annotatedStringBuilder, it, depth + 1) }
        }
        return annotatedStringBuilder
    }

    @Composable
    override fun append(annotatedStringBuilder: AnnotatedString.Builder, feature: IListComponent) =
        append(annotatedStringBuilder, feature.list)
}
