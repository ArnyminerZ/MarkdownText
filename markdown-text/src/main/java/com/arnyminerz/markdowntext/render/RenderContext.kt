package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntSize
import com.arnyminerz.markdowntext.MarkdownViewModel

internal interface RenderContext {
    val textSize: IntSize

    val annotatedStringBuilder: AnnotatedString.Builder

    val inlineContentMap: MutableMap<String, InlineTextContent>

    val viewModel: MarkdownViewModel

    companion object {
        inline fun provide(
            annotatedStringBuilder: AnnotatedString.Builder,
            textSize: IntSize,
            inlineContentMap: MutableMap<String, InlineTextContent>,
            viewModel: MarkdownViewModel,
            block: RenderContext.() -> Unit
        ) {
            block(
                object : RenderContext {
                    override val textSize: IntSize = textSize

                    override val annotatedStringBuilder: AnnotatedString.Builder = annotatedStringBuilder

                    override val inlineContentMap: MutableMap<String, InlineTextContent> = inlineContentMap

                    override val viewModel: MarkdownViewModel = viewModel
                }
            )
        }
    }
}
