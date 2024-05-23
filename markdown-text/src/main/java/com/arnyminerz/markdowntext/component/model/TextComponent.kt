package com.arnyminerz.markdowntext.component.model

import com.arnyminerz.markdowntext.companionClass
import com.arnyminerz.markdowntext.findChildOfType
import com.arnyminerz.markdowntext.flatChildren
import com.arnyminerz.markdowntext.hasChildWithName
import com.arnyminerz.markdowntext.hasChildren
import com.arnyminerz.markdowntext.name
import com.arnyminerz.markdowntext.processor.ProcessingContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

abstract class TextComponent private constructor() : IComponent {
    /**
     * Represents a TextComponent which only has one character, for example `:` are parsed as is, and not as TEXT
     * components.
     * Line breaks and white spaces.
     * @param name The name of the node that loads the component.
     * @param character The character that the class represents.
     */
    abstract class Mono(
        override val name: String,
        character: Char = name[0]
    ) : TextComponent(), FeatureCompanion {
        override val text: String = character.toString()

        companion object : NodeTypeCheck, NodeExtractor<Mono> {
            fun isInstanceOf(component: TextComponent): Boolean {
                if (component.text.length == 1) {
                    val companion = component::class.companionClass
                        ?.getDeclaredConstructor()
                        ?.newInstance()
                        as FeatureCompanion?
                        ?: return false
                    if (companion.name.length == 1) {
                        return true
                    }
                } else when (component) {
                    is EOL -> return true
                    is WS -> return true
                    is BR -> return true
                }
                return false
            }

            override fun ProcessingContext.isInstanceOf(instance: ASTNode): Boolean {
                val name = instance.name
                when {
                    name.length == 1 -> return true
                    name == EOL.name -> return true
                    name == WS.name -> return true
                    name == BR.name -> return true
                }
                return false
            }

            override fun ProcessingContext.extract(node: ASTNode): Mono {
                return object : Mono(node.name) {}
            }
        }
    }

    /** Alias for End Of Line (`EOL`) */
    object EOL : Mono("EOL", '\n')

    /** Alias for White Space (`WHITE_SPACE`) */
    object WS : Mono("WHITE_SPACE", ' ')

    /** Alias for Backslash (`BR`) */
    object BR : Mono("BR", '\\')

    /** Alias for Backtick (`BACKTICK`) */
    object BACKTICK : Mono("BACKTICK", '`')

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

            override fun ProcessingContext.isInstanceOf(instance: ASTNode): Boolean {
                val name = instance.name
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

            override fun ProcessingContext.isInstanceOf(instance: ASTNode): Boolean {
                return instance.name == name && instance.hasChildWithName(Text.name)
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

            override fun ProcessingContext.isInstanceOf(instance: ASTNode): Boolean {
                return instance.name == name &&
                    instance.hasChildWithName(LINK_TEXT) &&
                    instance.hasChildWithName(LINK_DESTINATION) ||
                    instance.name == GFM_AUTOLINK
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
                val destinationNode = node.findChildOfType(LINK_DESTINATION)
                val destination = destinationNode
                    ?.findChildOfType(GFM_AUTOLINK)
                    ?.getTextInNode()
                    ?: destinationNode
                        ?.getTextInNode()
                        ?.toHttpUrlOrNull()
                        ?.toString()
                    ?: error("Could not find a $LINK_DESTINATION > $GFM_AUTOLINK node inside of the link.")
                return Link(text, destination)
            }
        }
    }

    data class Checkbox(
        val isChecked: Boolean
    ) : TextComponent() {
        companion object : NodeTypeCheck, NodeExtractor<Checkbox> {
            private const val SHORT_REFERENCE_LINK = "SHORT_REFERENCE_LINK"

            private const val CHECKBOX_PREFIX_LENGTH = 3

            /**
             * Checkboxes get processed either as:
             * - `[`
             * - `WHITE_SPACE`
             * - `]`
             *
             * when not checked, or:
             * - `SHORT_REFERENCE_LINK`
             *     - `LINK_LABEL`
             *         - `[`
             *         - `TEXT` (`x`)
             *         - `]`
             */
            override fun ProcessingContext.isInstanceOf(instance: ASTNode): Boolean {
                if (instance.name == SHORT_REFERENCE_LINK) {
                    val text = instance.getTextInNode()
                    if (text[0] != '[') return false
                    if (text[2] != ']') return false
                    return true
                }

                // Only allow detection of parent checkbox for the first iteration of the array,
                // otherwise the text is never detected and/or added
                val isFirst = instance.startOffset == instance.parent?.startOffset
                if (!isFirst) return false

                return instance.parent?.children?.let { nodes ->
                    nodes.size >= CHECKBOX_PREFIX_LENGTH &&
                        nodes[0].name == "[" &&
                        nodes[1].name == WS.name &&
                        nodes[2].name == "]"
                } == true
            }

            override fun ProcessingContext.extract(node: ASTNode): Checkbox {
                val text = if (node.name == SHORT_REFERENCE_LINK)
                    node.getTextInNode()
                else
                    node.parent!!.getTextInNode()
                return Checkbox(
                    isChecked = text[1] != ' '
                )
            }
        }

        override val text: String = ""
    }

    abstract val text: String

    override fun toString(): String = text
}
