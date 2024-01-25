package com.arnyminerz.markdowntext.component.model

import org.intellij.markdown.ast.ASTNode

interface NodeTypeCheck {
    fun isInstanceOf(node: ASTNode): Boolean
}
