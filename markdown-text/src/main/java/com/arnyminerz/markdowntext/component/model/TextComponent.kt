package com.arnyminerz.markdowntext.component.model

import com.arnyminerz.markdowntext.findChildOfType
import com.arnyminerz.markdowntext.flatChildren
import com.arnyminerz.markdowntext.hasChildWithName
import com.arnyminerz.markdowntext.hasChildren
import com.arnyminerz.markdowntext.name
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

abstract class TextComponent private constructor() : IComponent {
    /** Alias for End Of Line */
    object EOL : TextComponent() {
        const val name: String = "EOL"

        override val text: String = "\n"
    }

    /** Alias for White Space */
    object WS : TextComponent() {
        const val name: String = "WHITE_SPACE"

        override val text: String = " "
    }

    class Text(override val text: String) : TextComponent() {
        companion object : FeatureCompanion {
            override val name: String = "TEXT"
        }
    }

    data class StyledText(
        override val text: String,
        val isBold: Boolean,
        val isItalic: Boolean,
        val isStrikethrough: Boolean
    ) : TextComponent() {
        companion object : NodeTypeCheck, NodeExtractor<StyledText> {
            /** `STRONG` */
            const val NODE_BOLD = "STRONG"

            /** `EMPH` */
            const val NODE_ITAL = "EMPH"

            /** `STRIKETHROUGH` */
            const val NODE_STRI = "STRIKETHROUGH"

            override fun isInstanceOf(node: ASTNode): Boolean {
                val name = node.name
                return name == NODE_BOLD || name == NODE_ITAL || name == NODE_STRI
            }

            override fun ProcessingContext.extract(node: ASTNode): StyledText {
                val text = node.findChildOfType(Text.name)
                checkNotNull(text) { "Got an styled text without a TEXT element in it: ${node.name}" }

                fun checkFormat(nodeType: String): Boolean {
                    // If the node is the correct type, just return true
                    if (node.name == nodeType) return true
                    return node
                        // Get all the children, including children inside of children
                        .flatChildren()
                        // Fetch only the types we want
                        .filter { it.name == nodeType }
                        // If there's a child type, it must have children.
                        // If the node doesn't have any children, it's just an indicator
                        .any(ASTNode::hasChildren)
                }

                val isBold = checkFormat(NODE_BOLD)
                val isItalic = checkFormat(NODE_ITAL)
                val isStrikethrough = checkFormat(NODE_STRI)

                return StyledText(
                    text.getTextInNode(allFileText).toString(),
                    isBold,
                    isItalic,
                    isStrikethrough
                )
            }
        }
    }

    abstract val text: String

    override fun toString(): String = text
}
