package com.arnyminerz.markdowntext

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import com.arnyminerz.markdowntext.component.Header
import com.arnyminerz.markdowntext.component.HorizontalRule
import com.arnyminerz.markdowntext.component.OrderedList
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.component.UnorderedList
import com.arnyminerz.markdowntext.component.model.IComponent
import com.arnyminerz.markdowntext.processor.IProcessor
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import com.arnyminerz.markdowntext.render.HeaderRenderer
import com.arnyminerz.markdowntext.render.HorizontalRuleRenderer
import com.arnyminerz.markdowntext.render.ListRenderer
import com.arnyminerz.markdowntext.render.ParagraphRenderer

/**
 * Displays a fully-capable container which renders Markdown content.
 * @param markdown The markdown text to display.
 *
 * **DO NOT SET AS SCROLLABLE, USES LAZY LISTS.**
 * @param modifier Any modifiers to apply to the whole container.
 * @param componentModifier Calculates the modifier to apply to each component loaded. It's recommended to leave on
 * `null` if the default behaviour is desired.
 * @param processor The processor to use for decoding the Markdown code.
 * @param isVertical If `true`, the content will be displayed vertically. Otherwise, it will be horizontal.
 */
@Composable
@ExperimentalTextApi
fun MarkdownContainer(
    markdown: String,
    modifier: Modifier = Modifier,
    componentModifier: ((IComponent) -> Modifier)? = null,
    processor: IProcessor = JetbrainsMarkdownProcessor(),
    isVertical: Boolean = true,
    state: LazyListState = rememberLazyListState()
) {
    val components = remember(markdown) { processor.load(markdown) }

    fun LazyListScope.drawComponents() {
        items(components) { component ->
            val mod = componentModifier?.invoke(component) ?: Modifier.fillMaxWidth()
            when (component) {
                is Paragraph -> with(ParagraphRenderer()) { Content(component, modifier = mod) }
                is OrderedList -> with(ListRenderer) { Content(component, modifier = mod) }
                is UnorderedList -> with(ListRenderer) { Content(component, modifier = mod) }
                is Header -> with(HeaderRenderer) { Content(component, modifier = mod) }
                is HorizontalRule -> with(HorizontalRuleRenderer) { Content(component, modifier = mod) }
                else -> Logger.error("Got unknown component: ${component::class.simpleName}")
            }
        }
    }

    if (isVertical) {
        LazyColumn(modifier, state = state) {
            drawComponents()
        }
    } else {
        LazyRow(modifier, state = state) {
            drawComponents()
        }
    }
}
