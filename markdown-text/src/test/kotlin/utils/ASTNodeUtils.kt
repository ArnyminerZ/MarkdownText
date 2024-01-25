package utils

import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

/** Instantiates a new [ASTNode]. */
fun buildASTNode(
    type: IElementType,
    startOffset: Int = 0,
    endOffset: Int = 0,
    parent: ASTNode? = null,
    children: List<ASTNode> = emptyList()
): ASTNode = object : ASTNode {
    override val type: IElementType = type
    override val startOffset: Int = startOffset
    override val endOffset: Int = endOffset
    override val children: List<ASTNode> = children
    override val parent: ASTNode? = parent
}
