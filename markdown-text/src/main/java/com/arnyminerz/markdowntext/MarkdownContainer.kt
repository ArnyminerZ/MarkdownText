package com.arnyminerz.markdowntext

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.LayoutDirection
import com.arnyminerz.markdowntext.component.Header
import com.arnyminerz.markdowntext.component.OrderedList
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.component.UnorderedList
import com.arnyminerz.markdowntext.component.model.IComponent
import com.arnyminerz.markdowntext.processor.IProcessor
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import com.arnyminerz.markdowntext.render.HeaderRenderer
import com.arnyminerz.markdowntext.render.ListRenderer
import com.arnyminerz.markdowntext.render.ParagraphRenderer

/**
 * Displays a fully-capable container which renders Markdown content.
 * @param markdown The markdown text to display.
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
    isVertical: Boolean = true
) {
    val components = remember(markdown) { processor.load(markdown) }

    @Composable
    fun Draw() {
        for (component in components) {
            val mod = componentModifier?.invoke(component) ?: Modifier.fillMaxWidth()
            when (component) {
                is Paragraph -> ParagraphRenderer().Content(component, modifier = mod)
                is OrderedList -> ListRenderer.Content(component, modifier = mod)
                is UnorderedList -> ListRenderer.Content(component, modifier = mod)
                is Header -> HeaderRenderer.Content(component, modifier = mod)
                else -> Log.e("MarkdownText", "Got unknown component: ${component::class.simpleName}")
            }
        }
    }

    if (isVertical) {
        Column(modifier) {
            Draw()
        }
    } else {
        Row(modifier) {
            Draw()
        }
    }
}
