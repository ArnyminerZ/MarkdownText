package com.arnyminerz.markdowntext.render

import android.util.Log
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.IntSize
import com.arnyminerz.markdowntext.MarkdownViewModel
import com.arnyminerz.markdowntext.component.CodeFence
import com.arnyminerz.markdowntext.component.Header
import com.arnyminerz.markdowntext.component.HorizontalRule
import com.arnyminerz.markdowntext.component.OrderedList
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.component.UnorderedList
import com.arnyminerz.markdowntext.component.model.Feature
import com.arnyminerz.markdowntext.render.style.TextStyles
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeThemeType

/**
 * Builds an annotated string from a list of features.
 * @param inlineContentMap The map of inline content to append.
 * @param textSize The size of the text. Usually a single `0` character, which is the maximum space
 * a character is supposed to use.
 * @param features The list of features to append.
 * @return The built annotated string.
 */
@ExperimentalTextApi
@Suppress("LongParameterList")
internal fun buildAnnotatedString(
    textSize: IntSize,
    textStyles: TextStyles,
    codeParser: PrettifyParser,
    codeThemeType: CodeThemeType,
    features: List<Feature>,
    viewModel: MarkdownViewModel
) = buildAnnotatedString {
    RenderContext.provide(
        this,
        textSize,
        textStyles,
        codeParser,
        codeThemeType,
        viewModel.inlineContentMap,
        viewModel
    ) {
        for (feature in features) {
            when (feature) {
                is Paragraph -> ParagraphRenderer().append(feature)
                is OrderedList -> ListRenderer.append(feature)
                is UnorderedList -> ListRenderer.append(feature)
                is Header -> HeaderRenderer.append(feature)
                is HorizontalRule -> HorizontalRuleRenderer.append(feature)
                is CodeFence -> CodeFenceRenderer.append(feature)
                else -> Log.e("MarkdownText", "Got unknown feature: ${feature::class.simpleName}")
            }
        }
    }
}
