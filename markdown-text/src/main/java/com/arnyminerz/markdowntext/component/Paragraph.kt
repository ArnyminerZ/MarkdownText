package com.arnyminerz.markdowntext.component

import com.arnyminerz.markdowntext.Logger
import com.arnyminerz.markdowntext.component.model.FeatureCompanion
import com.arnyminerz.markdowntext.component.model.IContainerCompanion
import com.arnyminerz.markdowntext.component.model.ITextContainer
import com.arnyminerz.markdowntext.component.model.NodeExtractor
import com.arnyminerz.markdowntext.component.model.NodeTypeCheck
import com.arnyminerz.markdowntext.component.model.TextComponent
import com.arnyminerz.markdowntext.component.model.TextComponent.Checkbox
import com.arnyminerz.markdowntext.component.model.TextComponent.CodeSpan
import com.arnyminerz.markdowntext.component.model.TextComponent.Image
import com.arnyminerz.markdowntext.component.model.TextComponent.Link
import com.arnyminerz.markdowntext.component.model.TextComponent.Mono
import com.arnyminerz.markdowntext.component.model.TextComponent.StyledText
import com.arnyminerz.markdowntext.component.model.TextComponent.Text
import com.arnyminerz.markdowntext.component.model.TextComponent.WS
import com.arnyminerz.markdowntext.name
import com.arnyminerz.markdowntext.processor.ProcessingContext
import okhttp3.internal.toImmutableList
import org.intellij.markdown.ast.ASTNode

data class Paragraph(
    override val list: List<TextComponent>
) : ITextContainer {
    companion object : FeatureCompanion, IContainerCompanion<TextComponent, Paragraph> {
        override val name: String = "PARAGRAPH"

        interface ComponentBuilder {
            fun ProcessingContext.instanceCheck(node: ASTNode): Boolean

            fun ProcessingContext.constructor(node: ASTNode): TextComponent
        }

        internal fun nameCheck(
            featureCompanion: FeatureCompanion,
            constructor: ProcessingContext.(ASTNode) -> TextComponent
        ): ComponentBuilder = object : ComponentBuilder {
            override fun ProcessingContext.instanceCheck(node: ASTNode): Boolean = node.name == featureCompanion.name

            override fun ProcessingContext.constructor(node: ASTNode): TextComponent {
                return constructor(this, node)
            }
        }

        private fun <C: TextComponent> nodeTypeCheck(
            checker: NodeTypeCheck,
            extractor: NodeExtractor<C>
        ): ComponentBuilder = object : ComponentBuilder {
            override fun ProcessingContext.instanceCheck(node: ASTNode): Boolean =
                with(checker) { isInstanceOf(node) }

            override fun ProcessingContext.constructor(node: ASTNode): TextComponent {
                return with(extractor) { extract(node) }
            }
        }

        @Suppress("SpreadOperator")
        private val components: Set<ComponentBuilder> = setOf(
            nodeTypeCheck(Checkbox, Checkbox),
            nameCheck(Text) { Text(it.getTextInNode()) },
            *TextComponent.monoComponentBuilders,
            nodeTypeCheck(Image, Image),
            nodeTypeCheck(Mono, Mono),
            nodeTypeCheck(CodeSpan, CodeSpan),
            nodeTypeCheck(StyledText, StyledText),
            nodeTypeCheck(Link, Link)
        )

        override fun ProcessingContext.explore(root: ASTNode): Paragraph {
            val list = mutableListOf<TextComponent>()
            for (node in root.children) {
                // Find a component that fulfills its condition
                val entry = components.find {
                    with (it) { instanceCheck(node) }
                } ?: let {
                    Logger.warning("Got an invalid component: ${node.name}")
                    null
                } ?: continue
                // Execute the component's callback, or throw
                val component = with(entry) { constructor(node) }
                list.add(component)
            }
            return Paragraph(list)
        }
    }

    val lines: List<String> = list.joinToString("") { it.text }.split('\n')

    override fun toString(): String = list.joinToString("") { it.toString() }

    operator fun plus(other: Paragraph): Paragraph {
        val result = list.toMutableList()
        result.addAll(other.list)
        return Paragraph(result)
    }

    fun trimStartWS(): Paragraph {
        // Remove all the elements from the beginning of list that are WS
        val newList = list.toMutableList()
        for (item in list) {
            if (item !is WS) break
            newList.remove(item)
        }
        return Paragraph(
            list = newList.toImmutableList()
        )
    }
}

/**
 * Uses [Paragraph.plus] to join all the elements of the paragraph.
 * @throws IllegalStateException If the list is empty.
 */
fun List<Paragraph>.join(): Paragraph {
    check(isNotEmpty()) { "Cannot join an empty list of paragraphs." }
    val iterator = iterator()
    var builder = iterator.next()
    while (iterator.hasNext()) {
        builder += iterator.next()
    }
    return builder
}

/**
 * Results a new list in which each element is the result of adding ([Paragraph.plus]) the
 * paragraphs of the top list.
 */
fun List<List<Paragraph>>.flatten(): List<Paragraph> {
    val result = mutableListOf<Paragraph>()
    for (list in this) {
        if (list.isEmpty()) continue
        result.add(list.join())
    }
    return result
}
