package com.arnyminerz.markdowntext.processor

import com.arnyminerz.markdowntext.MarkdownFlavour
import com.arnyminerz.markdowntext.component.model.IComponent
import com.arnyminerz.markdowntext.component.MarkdownFile
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.name
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.parser.MarkdownParser

@Suppress("MagicNumber")
class JetbrainsMarkdownProcessor(
    private val flavour: MarkdownFlavour
) : IProcessor {
    private fun logNode(node: ASTNode, allFileText: String, depth: Int = 0) {
        val prefix = "  ".repeat(depth)
        var text = node.getTextInNode(allFileText)
        if (text.length > 16) text = text.substring(0, minOf(48, text.length)) + " ..."
        println("$prefix- ${node.name} [${node.startOffset}-${node.endOffset}] (${node.children.size}) - $text")
        for (child in node.children) logNode(child,  allFileText, depth + 1)
    }

    private fun ProcessingContext.explode(node: ASTNode, depth: Int = 0): List<IComponent> {
        val prefix = "  ".repeat(depth)
        println("$prefix- ${node.name} [${node.startOffset}-${node.endOffset}] (${node.children.size})")

        val features = mutableListOf<IComponent>()
        when (node.name) {
            Paragraph.name -> with(Paragraph) { explore(node) }.let(features::add)
            // If not handled, keep getting deeper
            else -> for (child in node.children) explode(child, depth + 1).let(features::addAll)
        }
        return features
    }

    override fun load(markdown: String): List<IComponent> {
        val parsedTree = MarkdownParser(flavour.descriptor).buildMarkdownTreeFromString(markdown)

        check(parsedTree.name == MarkdownFile.name) { "Root element must be MARKDOWN_FILE" }

        println("Loaded data:")
        logNode(parsedTree, markdown)
        println()

        val context = ProcessingContext.build(markdown)
        return context.explode(parsedTree)
    }
}
