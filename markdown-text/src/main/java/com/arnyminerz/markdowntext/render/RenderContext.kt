package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntSize
import com.arnyminerz.markdowntext.MarkdownViewModel
import com.arnyminerz.markdowntext.render.style.TextStyles
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeThemeType

internal interface RenderContext {
    val textSize: IntSize

    val textStyles: TextStyles

    val annotatedStringBuilder: AnnotatedString.Builder

    val inlineContentMap: MutableMap<String, InlineTextContent>

    val viewModel: MarkdownViewModel

    val codeParser: PrettifyParser

    val codeThemeType: CodeThemeType

    companion object {
        @Suppress("LongParameterList")
        inline fun provide(
            annotatedStringBuilder: AnnotatedString.Builder,
            textSize: IntSize,
            textStyles: TextStyles,
            codeParser: PrettifyParser,
            codeThemeType: CodeThemeType,
            inlineContentMap: MutableMap<String, InlineTextContent>,
            viewModel: MarkdownViewModel,
            block: RenderContext.() -> Unit
        ) {
            block(
                object : RenderContext {
                    override val textSize: IntSize = textSize

                    override val textStyles: TextStyles = textStyles

                    override val annotatedStringBuilder: AnnotatedString.Builder = annotatedStringBuilder

                    override val inlineContentMap: MutableMap<String, InlineTextContent> = inlineContentMap

                    override val viewModel: MarkdownViewModel = viewModel

                    override val codeParser: PrettifyParser = codeParser

                    override val codeThemeType: CodeThemeType = codeThemeType
                }
            )
        }
    }
}
