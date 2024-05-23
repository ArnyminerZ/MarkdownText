package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.arnyminerz.markdowntext.component.CodeFence
import com.arnyminerz.markdowntext.render.code.LocalCodeParser
import com.arnyminerz.markdowntext.render.code.LocalCodeTheme
import com.wakaztahir.codeeditor.highlight.model.CodeLang
import com.wakaztahir.codeeditor.highlight.utils.parseCodeAsAnnotatedString

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
        val theme = localTheme.theme()

        val code = remember {
            parseCodeAsAnnotatedString(
                localParser,
                theme,
                language,
                code = feature.lines.joinToString("\n")
            )
        }

        Text(text = code, modifier = modifier)
    }

    @Composable
    override fun append(
        annotatedStringBuilder: AnnotatedString.Builder,
        feature: CodeFence
    ): AnnotatedString.Builder {
        val language = feature.language ?: CodeLang.Markdown

        val localParser = LocalCodeParser.current
        val localTheme = LocalCodeTheme.current
        val theme = localTheme.theme()

        val code = remember {
            parseCodeAsAnnotatedString(
                localParser,
                theme,
                language,
                code = feature.lines.joinToString("\n")
            )
        }

        annotatedStringBuilder.append(code)

        return annotatedStringBuilder
    }
}
