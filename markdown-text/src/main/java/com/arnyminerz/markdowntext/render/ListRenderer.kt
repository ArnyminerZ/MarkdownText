package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arnyminerz.markdowntext.MarkdownViewModel
import com.arnyminerz.markdowntext.component.model.IListComponent
import com.arnyminerz.markdowntext.component.model.ListElement
import com.arnyminerz.markdowntext.ui.ExtendedClickableText
import com.arnyminerz.markdowntext.ui.utils.rememberMaxCharacterSize

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
    override fun LazyItemScope.Content(feature: IListComponent, modifier: Modifier) {
        val uriHandler = LocalUriHandler.current
        val style = LocalTextStyle.current

        val textSize = rememberMaxCharacterSize(style)

        val viewModel = viewModel<MarkdownViewModel>()
        val text = buildAnnotatedString(textSize, feature.list, viewModel)

        ExtendedClickableText(
            text = text,
            inlineContent = viewModel.inlineContentMap,
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

    context(RenderContext)
    @Composable
    private fun append(
        elements: List<ListElement>,
        depth: Int = 0
    ): AnnotatedString.Builder {
        val pad = paddingAlgorithm(depth)
        for (element in elements) {
            // Add the pad
            annotatedStringBuilder.append(pad)
            // Append the prefix string
            element.prefix?.let(annotatedStringBuilder::append)
            // Add a tab for giving a bit of space
            annotatedStringBuilder.append('\t')
            // Render the paragraph
            ParagraphRenderer(otherLinesPrefix = "$pad \t").append(element.paragraph)

            element.subList?.let { append(it, depth + 1) }
        }
        return annotatedStringBuilder
    }

    context(RenderContext)
    @Composable
    override fun append(feature: IListComponent) {
        append(feature.list)
    }
}
