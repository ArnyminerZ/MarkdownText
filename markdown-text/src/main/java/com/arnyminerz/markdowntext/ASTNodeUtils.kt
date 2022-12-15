package com.arnyminerz.markdowntext

import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

/**
 * Alias for [ASTNode.children].[Collection.isNotEmpty]
 */
internal fun ASTNode.isNotEmpty() = children.isNotEmpty()

internal fun ASTNode.flatChildren(): List<ASTNode> {
    val list = arrayListOf<ASTNode>()
    list.add(this)
    if (isNotEmpty())
        list.addAll(children.map { it.flatChildren() }.flatten())
    return list
}

internal fun ASTNode.findChildOfType(tagName: String) = flatChildren().find { it.name == tagName }

internal fun ASTNode.getNodeLinkText(allFileText: CharSequence) = getTextInNode(allFileText)
    .trimStart { it == '[' }
    .trimEnd { it == ']' }

internal fun ASTNode.hasChildWithName(nodeName: String): Boolean = findChildOfType(nodeName) != null

/**
 * Searches for a parent with the given [ASTNode.name].
 * @since 20221215
 */
internal fun ASTNode?.findParentWithName(nodeName: String): ASTNode? =
    this?.parent?.let { parentNode ->
        parentNode.takeIf { it.name == nodeName } ?: parentNode.findParentWithName(nodeName)
    }

/**
 * Checks if the node contains any parent with the given name..
 * @since 20221215
 */
internal fun ASTNode?.hasParentWithName(nodeName: String): Boolean =
    findParentWithName(nodeName) != null

/**
 * Alias for [ASTNode.type].[IElementType.name].
 */
internal val ASTNode.name: String
    get() = type.name
