package com.arnyminerz.markdowntext.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.arnyminerz.markdowntext.component.model.IListComponent
import com.arnyminerz.markdowntext.component.model.ListElement

object ListRenderer : IRenderer<IListComponent> {
    @Composable
    override fun Content(feature: IListComponent) {
        TODO("Not yet implemented")
    }

    private fun append(
        annotatedStringBuilder: AnnotatedString.Builder,
        elements: List<ListElement>,
        depth: Int = 0
    ) {
        val pad = "  ".repeat(depth)
        for (element in elements) {
            // Add the pad
            annotatedStringBuilder.append(pad)
            // Append the prefix string
            annotatedStringBuilder.append(element.prefix)
            // Add a space for giving a bit of space
            annotatedStringBuilder.append(" ")
            // Render the paragraph
            ParagraphRenderer.append(annotatedStringBuilder, element.paragraph)
            // And add a line break
            annotatedStringBuilder.appendLine()

            element.subList?.let { append(annotatedStringBuilder, it, depth + 1) }
        }
    }

    override fun append(annotatedStringBuilder: AnnotatedString.Builder, feature: IListComponent) {
        append(annotatedStringBuilder, feature.list)
    }
}
