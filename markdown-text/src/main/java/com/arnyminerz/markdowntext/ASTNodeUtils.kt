package com.arnyminerz.markdowntext

import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

/**
 * Alias for [ASTNode.children].[Collection.isNotEmpty]
 */
fun ASTNode.isNotEmpty() = children.isNotEmpty()

fun ASTNode.findChildOfType(tagName: String) = children.find { it.name == tagName }

fun ASTNode.getNodeLinkText(allFileText: CharSequence) = getTextInNode(allFileText)
    .trimStart { it == '[' }
    .trimEnd { it == ']' }

/**
 * Alias for [ASTNode.type].[IElementType.name].
 */
val ASTNode.name: String
    get() = type.name
