package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntSize
import com.arnyminerz.markdowntext.component.CodeFence
import com.arnyminerz.markdowntext.render.code.LocalCodeParser
import com.arnyminerz.markdowntext.render.code.LocalCodeTheme
import com.wakaztahir.codeeditor.model.CodeLang
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString

/**
 * Renders a code fence feature into a composable element.
 * @see LocalCodeTheme
 * @see LocalCodeParser
 */
object CodeFenceRenderer : IRenderer<CodeFence> {
    @Composable
    override fun LazyItemScope.Content(feature: CodeFence, modifier: Modifier) {
        val language = feature.language ?: CodeLang.Markdown

        val localParser = LocalCodeParser.current
        val localTheme = LocalCodeTheme.current

        val code = remember {
            parseCodeAsAnnotatedString(
                localParser,
                localTheme.theme,
                language,
                code = feature.lines.joinToString("\n")
            )
        }

        Text(
            text = code,
            modifier = modifier,
            style = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
        )
    }

    context(RenderContext)
    @Composable
    override fun append(feature: CodeFence) {
        val language = feature.language ?: CodeLang.Markdown

        val localParser = LocalCodeParser.current
        val localTheme = LocalCodeTheme.current

        val code = remember {
            parseCodeAsAnnotatedString(
                localParser,
                localTheme.theme,
                language,
                code = feature.lines.joinToString("\n")
            )
        }

        annotatedStringBuilder.pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
        annotatedStringBuilder.append(code)
        annotatedStringBuilder.pop()
    }
}
