package com.arnyminerz.markdowntext

import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

/**
 * Alias for [ASTNode.children].[Collection.isNotEmpty]
 */
internal fun ASTNode.isNotEmpty() = children.isNotEmpty()

internal fun ASTNode.findChildOfType(tagName: String) = children.find { it.name == tagName }

internal fun ASTNode.getNodeLinkText(allFileText: CharSequence) = getTextInNode(allFileText)
    .trimStart { it == '[' }
    .trimEnd { it == ']' }

internal fun ASTNode.containsNodeWithName(nodeName: String): Boolean = children.any {
    if (it.isNotEmpty())
        it.containsNodeWithName(nodeName)
    else
        name == nodeName
}

/**
 * Searches for a parent with the given [ASTNode.name].
 * @since 20221215
 */
internal fun ASTNode?.findParentWithName(nodeName: String): ASTNode? =
    this?.parent?.let { parentNode ->
        parentNode.takeIf { it.name == nodeName } ?: parentNode.findParentWithName(nodeName)
    }

/**
 * Alias for [ASTNode.type].[IElementType.name].
 */
internal val ASTNode.name: String
    get() = type.name
