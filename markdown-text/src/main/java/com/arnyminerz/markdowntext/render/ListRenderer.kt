package com.arnyminerz.markdowntext.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.arnyminerz.markdowntext.component.model.IListComponent
import com.arnyminerz.markdowntext.component.model.ListElement

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
    override fun Content(feature: IListComponent) {
        TODO("Not yet implemented")
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
