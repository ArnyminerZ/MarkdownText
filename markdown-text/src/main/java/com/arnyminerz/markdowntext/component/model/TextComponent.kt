package com.arnyminerz.markdowntext.component.model

import com.arnyminerz.markdowntext.companionClass
import com.arnyminerz.markdowntext.findChildOfType
import com.arnyminerz.markdowntext.flatChildren
import com.arnyminerz.markdowntext.hasChildWithName
import com.arnyminerz.markdowntext.hasChildren
import com.arnyminerz.markdowntext.name
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode

abstract class TextComponent private constructor() : IComponent {
    abstract class SingleCharacterTextObject(
        override val name: String,
        override val text: String = name
    ) : TextComponent(), FeatureCompanion {
        companion object {
            fun isInstanceOf(component: TextComponent): Boolean {
                if (component.text.length == 1) {
                    val companion = component::class.companionClass
                        ?.getDeclaredConstructor()
                        ?.newInstance()
                            as FeatureCompanion?
                        ?: error("Could not find a companion for the given component (${component::class.simpleName})")
                    if (companion.name.length == 1) {
                        return true
                    }
                } else when (component) {
                    is EOL -> return true
                    is WS -> return true
                }
                return false
            }
        }
    }

    /** Alias for End Of Line */
    object EOL : SingleCharacterTextObject("EOL", "\n")

    /** Alias for White Space */
    object WS : SingleCharacterTextObject("WHITE_SPACE", " ")

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
                    text.getTextInNode(),
                    isBold,
                    isItalic,
                    isStrikethrough
                )
            }
        }
    }

    data class CodeSpan(
        override val text: String
    ) : TextComponent() {
        companion object : FeatureCompanion, NodeTypeCheck, NodeExtractor<CodeSpan> {
            override val name: String = "CODE_SPAN"

            override fun isInstanceOf(node: ASTNode): Boolean {
                return node.name == name && node.hasChildWithName(Text.name)
            }

            override fun ProcessingContext.extract(node: ASTNode): CodeSpan {
                val textNode = node.findChildOfType(Text.name)!!
                return CodeSpan(
                    text = textNode.getTextInNode()
                )
            }
        }
    }

    data class Link(
        override val text: String,
        val url: String
    ) : TextComponent() {
        companion object : FeatureCompanion, NodeTypeCheck, NodeExtractor<Link> {
            override val name: String = "INLINE_LINK"

            private const val LINK_TEXT = "LINK_TEXT"

            private const val LINK_DESTINATION = "LINK_DESTINATION"
            private const val GFM_AUTOLINK = "GFM_AUTOLINK"

            override fun isInstanceOf(node: ASTNode): Boolean {
                return node.name == name &&
                        node.hasChildWithName(LINK_TEXT) &&
                        node.hasChildWithName(LINK_DESTINATION) ||
                        node.name == GFM_AUTOLINK
            }

            override fun ProcessingContext.extract(node: ASTNode): Link {
                if (node.name == GFM_AUTOLINK) {
                    val autolink = node.getTextInNode()
                    return Link(autolink, autolink)
                }

                val text = node
                    .findChildOfType(LINK_TEXT)
                    ?.findChildOfType(Text.name)
                    ?.getTextInNode()
                    ?: error("Could not find a $LINK_TEXT node inside of the link.")
                val destination = node
                    .findChildOfType(LINK_DESTINATION)
                    ?.findChildOfType(GFM_AUTOLINK)
                    ?.getTextInNode()
                    ?: error("Could not find a $LINK_DESTINATION > $GFM_AUTOLINK node inside of the link.")
                return Link(text, destination)
            }
        }
    }

    abstract val text: String

    override fun toString(): String = text
}
