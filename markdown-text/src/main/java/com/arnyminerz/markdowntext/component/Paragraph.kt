package com.arnyminerz.markdowntext.component

import com.arnyminerz.markdowntext.component.model.FeatureCompanion
import com.arnyminerz.markdowntext.component.model.IContainerCompanion
import com.arnyminerz.markdowntext.component.model.ITextContainer
import com.arnyminerz.markdowntext.component.model.NodeExtractor
import com.arnyminerz.markdowntext.component.model.NodeTypeCheck
import com.arnyminerz.markdowntext.component.model.TextComponent
import com.arnyminerz.markdowntext.component.model.TextComponent.CodeSpan
import com.arnyminerz.markdowntext.component.model.TextComponent.Colon
import com.arnyminerz.markdowntext.component.model.TextComponent.EOL
import com.arnyminerz.markdowntext.component.model.TextComponent.Link
import com.arnyminerz.markdowntext.component.model.TextComponent.StyledText
import com.arnyminerz.markdowntext.component.model.TextComponent.Text
import com.arnyminerz.markdowntext.component.model.TextComponent.WS
import com.arnyminerz.markdowntext.name
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode

private typealias Component = Pair<(ASTNode) -> Boolean, ProcessingContext.(ASTNode) -> TextComponent>

data class Paragraph(
    override val list: List<TextComponent>
) : ITextContainer {
    companion object : FeatureCompanion, IContainerCompanion<TextComponent, Paragraph> {
        override val name: String = "PARAGRAPH"

        private fun nameCheck(
            featureCompanion: FeatureCompanion,
            constructor: ProcessingContext.(ASTNode) -> TextComponent
        ): Component =
            { n: ASTNode -> n.name == featureCompanion.name } to { constructor(it) }

        private fun <C: TextComponent> typeCheck(
            checker: NodeTypeCheck,
            extractor: NodeExtractor<C>
        ): Component = { n: ASTNode -> checker.isInstanceOf(n) } to { with(extractor) { extract(it) } }

        private val components: Set<Component> = setOf(
            nameCheck(Text) { Text(it.getTextInNode()) },
            nameCheck(EOL) { EOL },
            nameCheck(WS) { WS },
            nameCheck(Colon) { Colon },
            typeCheck(CodeSpan, CodeSpan),
            typeCheck(StyledText, StyledText),
            typeCheck(Link, Link)
        )

        override fun ProcessingContext.explore(root: ASTNode): Paragraph {
            val list = mutableListOf<TextComponent>()
            for (node in root.children) {
                // Find a component that fulfills its condition
                val entry = components.find { it.first(node) }?.second
                // Execute the component's callback, or throw
                val component = entry?.let { it(this, node) } ?: error("Got an invalid component: ${node.name}")
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
